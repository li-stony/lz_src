#!/bin/bash

rsync -r -v /home/cussyou/lz_datas/notes root@lizl.me:/root/ &> /home/cussyou/logs/sync.log

rsync -r -v root@lizl.me:/root/notes/00todo /home/cussyou/lz_datas/notes/ >> /home/cussyou/logs/sync.log 2>&1
