#!/bin/bash
t=`date "+%Y%m%d %H:%M"`
echo "-- $t --"
h=`hostname`
cd /home/cussyou/lz-datas
git pull origin master
git add .
git commit -m "Auto commit from aws at $t on $h"
git push origin master