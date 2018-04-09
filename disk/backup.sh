#!/bin/bash

# set -x

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d start ==" 
root="/home/cussyou/lz_box"
backdir="/media/cussyou/ExtDatas/lizl"

items=('lz-n1' 'lz-n2' 'lz-src' 'lz-profile' 'lz-files')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -r --checksum --progress --copy-links  "$root/$i" "$backdir" 
done

items=('lz-picture' 'lz-video' 'lz-pri' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -r  --size-only --progress --copy-links  "$root/$i" "$backdir" 
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d end =="
echo -e "\n\n"
