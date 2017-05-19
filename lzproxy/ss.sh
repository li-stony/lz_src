#!/bin/bash

#unset http_proxy

ss=$(ps -A|grep sslocal)
if [ -n "$ss" ] 
then
    echo "existed: $ss"
else
    echo "start: sslocal"
    nohup ~/.local/bin/sslocal -c ~/.ssconfig.json &> ~/logs/ss.log &
fi

po=$(ps -A | grep polipo)
if [ -n "$po" ]
then
    echo "existed: $po"
else
    echo "start: polipo"
    polipo socksParentProxy=localhost:1080 logFile=~/logs/polipo.log &    
fi

#export http_proxy='http://localhost:8123'
#echo $http_proxy
    
# shouldn't run ss.sh directly.
# instead, like below.
# source /path/ss.sh
