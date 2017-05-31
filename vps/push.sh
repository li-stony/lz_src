#!/bin/bash

d=$(date +"%Y-%m-%d %H:%M")
echo "$d start" > /home/cussyou/logs/push.log

# backup local data to vps
# and remove deleted files
rsync -r -v --delete /home/cussyou/lz_datas/notes/01todo root@lizl.me:/root/notes/ >> /home/cussyou/logs/push.log 2>&1


d=$(date +"%Y-%m-%d %H:%M")
echo "$d end" >> /home/cussyou/logs/push.log
