#!/bin/bash

d=$(date +"%Y-%m-%d %H:%M")
echo "$d start" > /home/cussyou/logs/backup.log

# backup local data to vps
# and remove deleted files
rsync -r -v  /home/cussyou/lz-pri root@lizl.me:/root/ >> /home/cussyou/logs/backup.log 2>&1


d=$(date +"%Y-%m-%d %H:%M")
echo "$d end" >> /home/cussyou/logs/backup.log
