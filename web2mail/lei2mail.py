#!/usr/bin/python3
import datetime
import json
import urllib
import urllib.request
import re
import smtplib
import base64
import os
import os.path

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

def getpath():
    path = os.path.dirname(os.path.abspath(__file__))
    return path

historyFile = "leihistory.txt"

def getlast():
    f = open(getpath()+'/data/'+historyFile)
    line = f.read()
    line = line.rstrip('\n')
    m = re.match('(\S+)\s+(\S+)', line)
    last = m.group(2)
    f.close()
    return last
def updatelast(url, last):
    if last == None:
        return

    if len(last) == 0 :
        return


    f = open(getpath()+'/data/'+historyFile, 'w')
    line = url + ' ' + last
    f.write(line)
    
def sendmail(link, title, content):
    print(link, title)
    # config
    fp = open(getpath()+'/data/config.json', 'r')
    config = json.load(fp, encoding="utf8")
    smtp = config['mail']['smtp']
    port = config['mail']['port']
    print (smtp, port)
    server = smtplib.SMTP(smtp, port)
    #server = smtplib.SMTP.connect(config['mail']['smtp'],587)
    server.ehlo_or_helo_if_needed()
    if 'tls' in config['mail']:
        server.starttls()
    password = base64.b64decode(config['mail']['pass']).decode('utf8')
    server.login(config['mail']['user'], password)
    fmt = """\
From: lei2mail<{0}>
To: {1}
MIME-Version: 1.0
Content-Type: text/html; charset=utf-8
Subject: {2}
<html><body>
{3}
</body></html>
"""
    bs = title.encode('utf8')
    # print(bs)
    bs = base64.b64encode(bs)
    # print(bs)
    title = bs.decode()
    title = '=?UTF-8?B?' + title + '?='
    content = "<p>"+link+"</p>" + content
    msg = fmt.format(config['mail']['user'], config['mail']['to'], title, content )
    msg = msg.encode('utf8')
    server.sendmail(config['mail']['user'], config['mail']['to'], msg)
    server.quit()
    print("mail sent")

def getitem(url):
    print ('get item:',url)
    res = urllib.request.urlopen(url)
    body = res.read()
    s = body.decode('utf8')
    p = re.compile('<h2>(.+)</h2>')
    m = re.search(p, s)
    title = m.group(1)
    startpos = m.start(0)
    subs = s[startpos:]
    endnum = 0
    endpos = startpos
    while endnum < 1:
        m = re.search('(<div)|(</div>)', subs)
        token = m.group(0)
        endpos = endpos + m.end(0)
        subs = subs[m.end(0):]

        if token == '<div':
            endnum = endnum-1    
        elif token == '</div>':
            endnum = endnum+1
    
    if endpos > startpos:
        content = s[startpos:endpos-6]

    # print(content)
    # mail it
    sendmail(url, title, content)


def gethome(url):
    oldlast = getlast()
    print('old last:',oldlast)
    
    res = urllib.request.urlopen(url)
    body = res.read()
    s = body.decode('utf8')
    # print(len(s))
    p = re.compile(r'<h2>\s+<a href="(/posts/blog-\S+html)">\s+(\S+)\s+</a>\s+<div class="post-date">\s+<span class="glyphicon glyphicon-time"></span>\s+([0-9]{4}-[0-9]{2}-[0-9]{2})\s*</div>\s*</h2>',re.S|re.M)
    m = re.search(p,s)
    newlast = oldlast
    while m != None:
        item = m.group(1)
        title = m.group(2)
        time = m.group(3)
        print('parse item:', item)
        if time > newlast:
            newlast = time
            
        if time > oldlast:
            pass
            # 
            # getitem(url+item)
        else:
            break
        # next
        s = s[m.end(0):]
        # print(s)
        m = re.search(p, s)

    print('new last:',newlast)
    updatelast(url, newlast)
    
if __name__ == '__main__' :
    # url
    #fmt = 'http://zhangtielei.com/posts/page{0}/index.html'
    #for i in range(2, 7):
    #    url = fmt.format(8-i)
    #    last = gethome(url)
    # check first page
    url = 'http://zhangtielei.com'
    last = gethome(url)
