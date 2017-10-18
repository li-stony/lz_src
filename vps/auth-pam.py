#!/usr/bin/python3

import sys

keys = {'test':'testvbndfk'}

def check_auth(path):
    f = open(path)
    strs = f.read().splitlines()
    u = strs[0]
    p = strs[1]
    f.close()
    print(u,p)
    if u in keys  :
        v = keys[u]
        if v == p :
            return 0
        else :
            return 2
    else:
        return 1

if __name__ == '__main__':
    if len(sys.argv) < 2:
        exit(-1)
    else:
        code = check_auth(sys.argv[1])
        print(code)
        exit(code)