import itchat
from itchat.content import *

import os
import sys

import datetime


data_folder = '.'

@itchat.msg_register([PICTURE, RECORDING, ATTACHMENT, VIDEO])
def download_files(msg):
    nowstr = datetime.datetime.now().strftime("%Y%m%d-%H%M")
    msg.download(os.path.join(data_folder, nowstr+msg['FileName']))

@itchat.msg_register(TEXT, isGroupChat=True)
def text_reply(msg):
    if(msg.isAt):    #判断是否有人@自己
    #如果有人@自己，就发一个消息告诉对方我已经收到了信息
    #itchat.send_msg("我已经收到了来自{0}的消息，实际内容为{1}".format(msg['ActualNickName'],msg['Text']),toUserName=msg['FromUserName'])
    

    
def main(folder):
    global data_folder
    data_folder = folder
    itchat.auto_login(hotReload=True)
    itchat.run(blockThread=True)


if __name__ == '__main__':

    if len(sys.argv) < 2:
        print(sys.argv[0], ' data_folder')
    else :
        main(sys.argv[1])