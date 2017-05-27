#!/bin/bash

d=$(date +"%Y-%m-%d %H:%M")
echo "$d start" > /home/cussyou/logs/sync.log

# backup all local data to vps
# and remove deleted files
rsync -r -v --delete /home/cussyou/lz_datas/notes root@lizl.me:/root/ >> /home/cussyou/logs/sync.log 2>&1

# sync files added on vps directly to local machine
rsync -r -v root@lizl.me:/root/notes /home/cussyou/lz_datas/ >> /home/cussyou/logs/sync.log 2>&1

d=$(date +"%Y-%m-%d %H:%M")
echo "$d end" >> /home/cussyou/logs/sync.log