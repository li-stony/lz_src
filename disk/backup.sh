#!/bin/bash

root="/home/cussyou"
backdir="/media/cussyou/back"

items=('lz-datas' 'lz-src' 'lz-picture' 'lz-video' 'lz-pri' 'lz-books' 'lz-music')
echo ${items[@]}
for i in ${items[@]}
do
    echo $i
    rsync -v -t -r "$root/$i" "$backdir"
done
