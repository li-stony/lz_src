import itchat
from itchat.content import *

import os
import sys

import datetime


data_folder = '.'


def print_msg(msg):
    nowstr = datetime.datetime.now().strftime("%Y%m%d-%H%M")
    print(nowstr, msg['FromUserName'],  msg['ActualNickName'],msg['Text'], sep='|||')

@itchat.msg_register([PICTURE, RECORDING, ATTACHMENT, VIDEO])
def download_files(msg):
    nowstr = datetime.datetime.now().strftime("%Y%m%d-%H%M")
    msg.download(os.path.join(data_folder, nowstr+msg['FileName']))
    print(nowstr, msg['FromUserName'],  msg['ActualNickName'], msg['FileName'], sep='|||')

@itchat.msg_register(TEXT, isGroupChat=True)
def group_msg(msg):
    #print(msg)
    print('recv file msg:', msg['FileName', file=sys.stderr])
    print_msg(msg)

@itchat.msg_register(TEXT, isGroupChat=False)
def normal_msg(msg):
    #print(msg)
    print('recv text msg:', msg['Text'], file=sys.stderr)
    print_msg(msg)

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