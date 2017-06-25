#!/bin/bash

d=$(date +"%Y-%m-%d %H:%M")
echo "$d start" > /home/cussyou/logs/pull.log

# sync files added on vps directly to local machine
rsync -r -v root@lizl.me:/root/notes/01todo /home/cussyou/lz_datas/notes >> /home/cussyou/logs/pull.log 2>&1

d=$(date +"%Y-%m-%d %H:%M")
echo "$d end" >> /home/cussyou/logs/pull.log
