#!/bin/bash

#set -x

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d start ==" 

root="/home/cussyou"
root2="/home/cussyou/datas"
backdir="/home/cussyou/google_drive"


# check if google-drive-ocamlfuse exists
gfuse=`df | grep google-drive-ocamlfuse`
if [ -z "$gfuse" ]; then
    echo 'google drive not mount'
    exit -2
fi

# backup my notes and profiles
# items=('lz-profile lz-files lz-src')
items=('lz-datas lz-files lz-src')
echo ${items[@]}
for i in ${items[@]}
do
    echo "backup $root/$i"
    if [ ! -d "$root/$i" ]; then
        echo "no exist. $i"
	continue
    fi
    # rsync -v -r --size-only --progress  "$root/$i" "$backdir" 
    # rsync -t -v -r --progress  "$root/$i" "$backdir" 
    rsync -v -r --inplace --checksum --progress "$root/$i" "$backdir"
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d =="

# backup my binary files
items=('lz-picture' 'lz-video' 'lz-books' 'lz-music')
#items=('lz-video' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo "backup $root2/$i"
    if [ ! -d "$root2/$i" ]; then
        echo "no exist. $i"
	continue
    fi
    rsync -v -r --size-only --progress  "$root2/$i" "$backdir" 
    # rsync -t -v -r --progress  "$root/$i" "$backdir" 
    # rsync -v -r --checksum --progress "$root/$i" "$backdir"
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d end =="
echo -e "\n\n"
