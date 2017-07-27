#!/usr/bin/python3
import datetime
import json
import urllib
import urllib.request
import urllib.error
import re
import smtplib
import base64
import os
import os.path
import sys
import mailme
import gzip


homeurl="http://www.geekonomics10000.com/"

def getitem(index):
    url = homeurl+str(index)
    print(url)
    try:
        req = urllib.request.Request(url)
        req.add_header('Accept-encoding', 'gzip')
        res = urllib.request.urlopen(req)
        body = res.read()
        print("body len:",len(body))
        s = ''
        if res.info().get('Content-Encoding') == 'gzip':
            body = gzip.decompress(body)
            print("decompress body len:",len(body))
        s = body.decode('utf8')
        # print(s)
        p = re.compile('<h2 class="post-title">(.+)</h2>')
        m = re.search(p, s)
        title = m.group(1)
        p = re.compile('<p>（.+，([0-9]{4,4})年([0-9]{1,2})月([0-9]{1,2})日）</p>')
        m = re.search(p, s)
        if m != None:
            date = datetime.date(1970, 1, 1)
            year = int(m.group(1))
            month = int(m.group(2))
            day = int(m.group(3))
            date = datetime.date(year, month, day)
            title = date.isoformat()+" "+title
        else:
            p = re.compile("This entry was posted on (.+月 [0-9]{1,2}, [0-9]{4,4}),")
            m = re.search(p, s)
            if m != None:
                title = m.group(1)+" "+title
        
        print(title)
        mailme.sendmail(title, s)

        return 1
    except urllib.error.URLError as err:
        print(err)
        return 0
    except Exception as err :
        print(err)
        return -1

def getall(start, end):
    sum = 0
    for i in range(start, end):
        re = getitem(i)
        if re == -1:
            print("error: ",i)
        else:
            sum += re
    print(sum, " artile transported")

if __name__ == '__main__':
    start = 1
    end = 880
    if len(sys.argv) == 2:
        start = int(sys.argv[1])
    elif len(sys.argv) == 3:
        start = int(sys.argv[1])
        end = int(sys.argv[2])
    else:
        print(sys.argv[0], "[start [end]]")
        exit(-1)
    getall(start, end)