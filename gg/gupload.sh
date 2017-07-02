#!/bin/bash

export http_proxy='http://127.0.0.1:8123'
export https_proxy='http://127.0.0.1:8123'

gdrive sync list --no-header | while read line
do
    item=$(echo $line | awk '{print "/home/cussyou/" $2 " " $1}')
    echo "$item"
    code=1
    # loop until end
    num=0
    while [ $code -ne 0 ]
    do
	echo "$item" | xargs -n 2 gdrive sync upload  
	code=$?
	echo "sync exit: $code"
	sleep 10

	# 
	if [ $code -ne 0 ]
	then
	    num=$(( num + 1 ))
	fi
	echo "error times: $num"
	if [ $num -gt 10 ]
	then
	    break
	fi
    done    
done

