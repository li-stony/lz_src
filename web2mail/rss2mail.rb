#!/usr/bin/ruby
require 'rss'
require 'net/http'
require 'net/smtp'
require 'json'
require 'base64'
require 'time'

$mail_fmt = <<MSG_END
From: rss2mail<%s>
To: %s
MIME-Version: 1.0
Content-Type: text/html; charset=utf-8
Subject: %s
<p>%s</p>
<p>%s</p>
%s
MSG_END

def data_path()
  str = File.expand_path(File.dirname(__FILE__))
  return str + '/data/'
end

class MailClient
  def initialize()
    p = data_path() + 'config.json'
    f = File.open(p, 'r')
    str = f.read(nil)
    config = JSON.parse(str)
    @mail_config = config['mail']
    @mail_config['pass'] = Base64.decode64(@mail_config['pass'])
  end

  def start()
    begin
      puts "connecting smtp ..."
      @client = Net::SMTP.new(@mail_config['smtp'], @mail_config['port'])
      if @mail_config['ttl']
        @client.enable_starttls
      end
      @client = @client.start(@mail_config['smtp'],
                    @mail_config['user'],
                    @mail_config['pass'],
                    :login)
      return true
    rescue Exception => err
      puts "!!! connect smtp error"
      puts err
      @client = nil
      return false
    end
  end

  def stop()
    puts "disconnecting smtp ..."
    if @client != nil
      begin
        @client.finish
      rescue
      end
      @client = nil
    end
  end
  
  def post_mail(feedName, updated, title, body)
    begin
      t = Time.at(updated)
      time = t.iso8601
      title = '=?UTF-8?B?' + Base64.strict_encode64(title) + '?='
      message = $mail_fmt % [@mail_config['user'], @mail_config['to'], title, feedName, time, body]
      if @client != nil
        @client.sendmail(message, @mail_config['user'], @mail_config['to'])
      else
        puts "!!! mail client is nil"
      end
    rescue Exception => err
      puts err
      
    end
    
  end
  
end

class HistoryMgr
  
  def initialize()
    @dict = Hash.new  
  end

  def update(url, date)
    @dict[url] = date
  end

  def get(url)
    value = @dict[url]
    if value == nil
      value = 0
    end
    return value
  end
  
  def save()
    p = data_path() + 'history.txt';
    f = File.open(p, 'w')
    @dict.each_key do |key|
      f.write("#{key}\t|\t#{@dict[key]}\n")
    end
                       
    f.close()
  end
  
  def load()
    puts 'load history ...'
    p = data_path() + 'history.txt';
    if not File.exist?(p)
      return
    end
    f = File.open(p, 'r')
    f.each_line do |line|
      line = line.chomp
      tokens = line.split(/\s*\|\s*/)
      # puts tokens
      @dict[tokens[0]] = tokens[1].to_i
    end
    f.close()
  end
end

class RssFetcher
  def initialize()
    
    @running = true
    @hmgr = HistoryMgr.new
    @feeds = Array.new
    @mail = MailClient.new
  end

  def start()
    puts 'start ...'
    @running = true
    @hmgr.load()
    while @running
      puts "begin at #{Time.now.iso8601}"
      
      
      ok = @mail.start()
      
      if ok
        puts "smtp connected. checking feeds ..."
        load_feeds()
        if @feeds.count == 0
          @mail.post_mail("rss2mail", Time.now.to_i, "Warning No Feeds", "<p>no feeds</p>")
        end
        @feeds.each do |url|
          if not @running
            break
          end
          begin
            # url = 'http://livesino.net/feed'
            #puts url
            printf("fetching [%s]\n", url)
            uri = URI(url)
            res = Net::HTTP.get_response(uri)
            rss = res.body()
            feed = RSS::Parser.parse(rss)
            lastBuild = @hmgr.get(url)
            if feed.feed_type == 'rss'
              # check if channel updated
              feedBuildDate = 0
              if feed.channel.lastBuildDate != nil
                feedBuildDate = feed.channel.lastBuildDate.to_i
              elsif feed.channel.pubDate != nil
                feedBuildDate = feed.channel.pubDate.to_i
              end
              if feedBuildDate <= lastBuild
                printf("[#{url}] not update\n")
                next
              end
              puts "Feed: [#{feed.channel.title}] update at <#{feedBuildDate}>"
              items = Array.new
              feed.items.each do |item|
                items.insert(0, item)
              end
              items.each do |item|
                itemUpdated = item.pubDate.to_i
                if item.pubDate.to_i <= lastBuild
                  puts "[#{item.title}] not updated"
                  next
                end
                
                puts "post item: [#{item.title}]"
                body = nil
                if item.content_encoded == nil
                  # some rss only support description property
                  body = item.description
                elsif
                  # some rss write whole content here
                  body = item.content_encoded
                end
                #puts body
                @mail.post_mail(feed.channel.title, itemUpdated, item.title, body)
              end
              @hmgr.update(url, feedBuildDate)
            elsif feed.feed_type == 'atom'
              #puts feed.updated.methods.sort
              t = feed.updated.content
              feedBuildDate = t.to_i
              if feedBuildDate <= lastBuild
                printf("[#{url}] not update\n")
                next
              end
              puts "Feed: [#{feed.title.content}] update at <#{feedBuildDate}>"
              #puts feed.methods.sort
              #puts feed.entry.methods.sort
              items = Array.new
              feed.entries.each do |item|
                items.insert(0, item)
              end
              items.each do |item|
                
                title = item.title.content
                body = item.content.content
                itemUpdated = item.updated.content.to_i
                if itemUpdated <= lastBuild
                  puts "[#{title}] not updated"
                  next
                end
                puts "post item #{title}"
                @mail.post_mail(feed.title.content, itemUpdated, title, body)
              end
              @hmgr.update(url, feedBuildDate)
            else
              puts "invalid format [#{url}]"
            end
            
          rescue Exception => e
            puts e.message
            puts e.backtrace.inspect
          end
          
        end
        @mail.stop()

        puts "end at #{Time.now.iso8601}"
        begin
          # cnt = 1440 # too frequently
          cnt = 4320
          while @running && (cnt > 0)
            sleep(5)
            cnt = cnt - 1
          end
        rescue
          next
        end
        
      else
        puts "smtp not ok, wait ..."
        begin
          cnt = 120
          while @running && (cnt > 0)
            sleep(5)
            cnt = cnt - 1
          end
        rescue
          next
        end
      end
      
      
    end
    @hmgr.save()
    puts 'stop.'
    
  end
  def stop()
    @running = false
  end
  def load_feeds()
    begin
      puts 'load feeds ... '
      @feeds.clear
      #url = "https://raw.githubusercontent.com/li-stony/zl_src/master/web2mail/data/feeds.txt"
      url = "https://raw.githubusercontent.com/li-stony/web2mail/master/data/feeds.txt"
      uri = URI(url)
      puts uri  
      http = Net::HTTP.new(uri.host, uri.port)
      http.use_ssl = true
      req = Net::HTTP::Get.new(uri.request_uri)
      res = http.request(req)
      printf("respons: %d %s\n", res.code, res.msg)
      res.body.each_line do |line|
        line = line.chomp
        if line =~ /^\s*#/
          next
        end
        @feeds.push(line)
        puts line
      end
      puts 'feeds loaded'
    rescue Exception => e
      puts 'feeds not load from github'
      puts e
    end
  end	
end

$proxy = nil
if __FILE__ == $0

  Signal.trap("TERM") do
    if $proxy != nil
      $proxy.stop()
    end
  end
  Signal.trap("INT") do
    if $proxy != nil
      $proxy.stop()
    end
  end
  STDOUT.sync = true 
  $proxy = RssFetcher.new
  $proxy.start()
end
