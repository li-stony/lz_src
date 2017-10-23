#!/bin/bash


d=$(date +"%Y-%m-%d %H:%M")
echo "== $d start ==" 

root="/home/cussyou"
backdir="cussyou@gc.lizl.me:/home/cussyou/google_drive"

# backup my notes and profiles
items=('lz-datas lz-src')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    # rsync -v -r --size-only --progress  "$root/$i" "$backdir" 
    # rsync -t -v -r --progress  "$root/$i" "$backdir" 
    rsync -v -r --checksum --progress "$root/$i" "$backdir"
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d =="

# backup my binary files
items=('lz-picture' 'lz-video' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -r --size-only --progress  "$root/$i" "$backdir" 
    # rsync -t -v -r --progress  "$root/$i" "$backdir" 
    # rsync -v -r --checksum --progress "$root/$i" "$backdir"
done

d=$(date +"%Y-%m-%d %H:%M")
echo "== $d end =="
echo -e "\n\n"
