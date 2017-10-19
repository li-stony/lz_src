#!/bin/bash


d=$(date +"%Y-%m-%d %H:%M")
echo "== $d start ==" 

root="/home/cussyou"
backdir="cussyou@gc.lizl.me:/home/cussyou/google_drive"

items=('lz-datas' 'lz-src' 'lz-picture' 'lz-video' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -t -r --size-only "$root/$i" "$backdir" 
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d end =="
echo -e "\n\n"
