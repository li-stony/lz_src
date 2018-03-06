#!/bin/bash

set -x

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d start ==" 
root="/home/cussyou"
root2="/media/cussyou/datas"
backdir="/media/cussyou/Datas/lizl/backup"

items=('lz-datas' 'lz-src')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -t -r "$root/$i" "$backdir" 
done

items=('lz-picture' 'lz-video' 'lz-pri' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -t -r "$root2/$i" "$backdir" 
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d end =="
echo -e "\n\n"
