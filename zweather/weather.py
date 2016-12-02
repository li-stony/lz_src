#! /usr/bin/python3

import os
import sys
import datetime
import pathlib
import http.client
import json
import urllib
import urllib.request


if __name__ == '__main__':

    basePath = "."
    configPath = "."
    dataPath = "."
    # print time
    print(datetime.datetime.now())
    # print path
    if(len(sys.argv) > 1):
        basePath = sys.argv[1]
    else:
        basePath = os.path.abspath(__file__)
        basePath = str(pathlib.Path(basePath).parent)
    print(basePath) 

    #
    configPath = basePath + os.path.sep + 'config' 
    configFile = configPath + os.path.sep + 'config.json' 
    print(configFile)
    # read config
    tmp = open(configFile)
    data = tmp.read()
    tmp.close()
    config = json.loads(data, encoding='utf8')
    print(config)
    # read codes
    codes = dict()
    tmp = open(configPath + os.path.sep + 'code_map.txt', mode='rt')
    lines = tmp.readlines()
    tmp.close()
    for line in lines :
        line = line.replace('\n','')
        line = line.replace('\r', '')
        # print(line)
        pos = line.find(' ')
        key = line[0:pos]
        value = line[pos+1:]
        codes[key]=value
    print(codes)
    
    notifyCodes = '0','1','2','3','4','5','6','7','8','10','11','12','13','15','16','17','18','21','23','25','35','36','41','43','45'

    #
    baseUrl = 'https://query.yahooapis.com/v1/public/yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q='
    paramFmt = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"{0}, {1}\") and u=\'c\'";
    
    for city in config['all']:
        # query weather
        param = paramFmt.format(city, 'China')
        print(param)
        param = urllib.parse.quote(param, encoding='utf8')
        url = baseUrl + param
        print(url)
        response = urllib.request.urlopen(url)
        body = response.read()
        result = body.decode('utf8')
        print(result)
    
        notify = False
        d = json.loads(result, encoding='utf8')
        low = d['query']['results']['channel']['item']['forecast'][0]['low']
        high = d['query']['results']['channel']['item']['forecast'][0]['high']
        code = d['query']['results']['channel']['item']['forecast'][0]['code']
        windSpeed = d['query']['results']['channel']['wind']['speed']
        windDirection = d['query']['results']['channel']['wind']['direction']
        summary = '{0}: {1}; Temp:[{2}~{3}]; Wind:({4} {5})'.format(city, codes[code], low, high, windDirection, windSpeed)
        print(summary)
        if(notifyCodes.index(code)){
            notify = True
        }