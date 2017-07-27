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
import sys

fmt1 = """\
From: mail-me<{0}>
To: {1}
MIME-Version: 1.0
Content-Type: text/html; charset=utf-8
Subject: {2}
{3}
"""

def getpath():
    path = os.path.dirname(os.path.abspath(__file__))
    return path

def sendmail(title, content):
    print(title)
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
    
    bs = title.encode('utf8')
    # print(bs)
    bs = base64.b64encode(bs)
    # print(bs)
    title = bs.decode()
    title = '=?UTF-8?B?' + title + '?='
    if '</html>' in content :
        pass
    elif '</body>' in content:
        content = "<html>\n" + content + "</html>\n"
    else:
        content = "<html><body>\n" + content + "</body></html>\n"

    msg = fmt1.format(config['mail']['user'], "cussyou+todo@gmail.com", title, content )
    msg = msg.encode('utf8')
    server.sendmail(config['mail']['user'], "cussyou+todo@gmail.com", msg)
    server.quit()
    print("mail sent")


if __name__ == '__main__':
    if len(sys.argv) == 3:
        sendmail(sys.argv[1], sys.argv[2])
    elif len(sys.argv) == 2:
        message = sys.stdin.read()
        sendmail(sys.argv[1], message)
    else:
        print(sys.argv[0], "<title> <message>")
        print(sys.argv[0], "<title> <<<EOF" )