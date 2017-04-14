#!/usr/bin/python3

import os
import sys
from PIL import Image
import datetime
import shutil
import os.path
import struct

folder = '.'

# http://stackoverflow.com/questions/21355316/getting-metadata-for-mov-video
# http://stackoverflow.com/questions/21381652/python-find-record-time-of-mp4-movie?noredirect=1&lq=1
# mp4 is based on mov.
def getmovctime(file):
    ATOM_HEADER_SIZE = 8
    EPOCH_ADJUSTER = 2082844800
    f = open(file, 'rb')
    while True:
        atom_header = f.read(ATOM_HEADER_SIZE)
        # print(atom_header)
        if atom_header[4:8] == b'moov':
            break;
        else:
            # print(type(atom_header))
            # print(len(atom_header))
            atom_size = struct.unpack(">I", atom_header[0:4])[0]
            f.seek(atom_size - 8, 1)
    # found 'moov' , look for 'mvhd' and timestamps
    atom_header = f.read(ATOM_HEADER_SIZE)
    if atom_header[4:8] == b'cmov':
        print('moov atom is compressed')
        return 0
    elif atom_header[4:8] != b'mvhd':
        print ("expected to find 'mvhd' header")
        return 0
    else:
        f.seek(4,1)
        creation_date = struct.unpack(">I", f.read(4))[0]
        modification_date = struct.unpack(">I", f.read(4))[0]

        t = datetime.datetime.fromtimestamp(creation_date - EPOCH_ADJUSTER)
        return t

        
def getctime(file):
    v = os.path.getctime(file)
    t = datetime.datetime.fromtimestamp(v)
    return t

def putother(file):
    dp = os.path.join(folder, 'unsort')
    print(dp)
    os.makedirs(dp, exist_ok=True)
    shutil.copy(file, dp, follow_symlinks=False)

def analyze(file):
    print(file)
    tmp = file.lower()
    t = 0
    if tmp.endswith('jpg') or tmp.endswith('jpeg') :
        im = Image.open(file)
        key = 36867
        if key in im._getexif():
            v = im._getexif()[key]
            print(v)
            t = datetime.datetime.strptime(v, '%Y:%m:%d %H:%M:%S')
        else:
            # t = getctime(file)
            putother(file)
            return
    elif tmp.endswith('png'):
        putother(file)
        return
    elif tmp.endswith('mov') or tmp.endswith('mp4'):
        t = getmovctime(file)
    else:
        t = getctime(file)

    # print(t)
    year = t.strftime("%Y")
    month = t.strftime("%m")
    day = t.strftime("%Y%m%d")
    dp = os.path.join(folder, year,month, day)
    print(dp)
    os.makedirs(dp, exist_ok=True)
    shutil.copy(file, dp, follow_symlinks=False)
    
def main(files):
    for file in files:
        analyze(file)

    
if __name__ == '__main__':
    if len(sys.argv) == 1:
        print(sys.argv[0], "[-d target-folder] files ...")
    else:
        files = list()
        if sys.argv[1] == '-d':
            folder = sys.argv[2]
            files = sys.argv[3:]
        else:
            files = sys.argv[2:]
            
        main(files)
