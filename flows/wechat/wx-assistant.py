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