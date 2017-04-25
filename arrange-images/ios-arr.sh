#!/bin/bash

# get last image file 's name

# test arguments

if [ $# -ne 2 ] ; then
    echo "$0 source-dir target-dir"
    exit 1
fi

if [ ! -d $1 ] ; then
    echo "error: $1 is not directory"
    exit 1
fi
if [ ! -d $2 ] ; then
    echo "error: $2 is not directory"
    exit 1
fi

lastfile=""
lastday=`find $2 -type d | sort -r | head -n 1`
# test if available
re=`echo $lastday | grep -E "/([0-9]{4})/([0-9]{2})/\1\2[0-9]{2}.*$"`

if [ -z $re ] ; then
    echo "error: $lastday"
    exit 1
fi

lastfile=`ls -1 $lastday | sort -r | head -n 1`
echo "last: $lastfile"

pyfile=$(dirname $(readlink -f $0))
pyfile="$pyfile/arrange.py"
echo "script: $pyfile/arrange.py"

files=`ls -1 $1 | sort -r`
#echo $files
for file in $files
do
    if [[ "$file" > "$lastfile" ]] ; then
	dest=$(echo $1/$file | sed 's/\/\//\//g')
	# echo $dest
	python3 $pyfile -d "$2" $dest
    else
	echo "done"
	exit 0
    fi
done
