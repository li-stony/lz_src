#!/bin/bash

export http_proxy='http://127.0.0.1:8123'
export https_proxy='http://127.0.0.1:8123'
echo "------"
t=$(date +"%Y-%m-%d %H:%M:%S")
echo "$t start ..."
# sync datas except picture and videos
gdrive sync list --no-header |sed -E '/[[:space:]]+[0-9]{4,4}[[:space:]]+/d' | sed -E '/lz-picture/d'|sed -E '/lz-video/d' | while read line
do
    item=$(echo $line | awk '{print "/home/cussyou/" $2 " " $1}')
    
    echo "$item"
    code=1
    # loop until end
    num=0
    while [ $code -ne 0 ]
    do
	echo "$item" | xargs -n 2 /home/cussyou/opt/gopath/bin/gdrive sync upload --chunksize 4096 --timeout 150
	code=$?
	echo "sync exit: $code"
	sleep 10

	# 
	if [ $code -ne 0 ]
	then
	    num=$(( num + 1 ))
	else
	    num=0
	fi
	echo "error times: $num"
	if [ $num -gt 20 ]
	then
	    break
	fi
    done    
done

t=$(date +"%Y-%m-%d %H:%M:%S")
echo "$t end"

