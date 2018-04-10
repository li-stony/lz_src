import datetime
import json
import urllib
import urllib.request
import re
import time
import sys

def get_data(result):
    # beijing url
    url = 'https://api.waqi.info/api/feed/@1451/obs.cn.json'
    req = urllib.request.Request(
        url, 
        data=None, 
        headers={
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.47 Safari/537.36'
        }
    )

    res = urllib.request.urlopen(req, timeout=600)
    body = res.read()
    s = body.decode('utf8')
    
    try:
        json_data = json.loads(s)
        out = json.dumps(json_data, indent=4)
        # print(out)
        # city
        obs = json_data['rxs']['obs']
        msg = obs[0]['msg']
        city = msg['city']['name']
        city_url = msg['city']['url']
        aqi = msg['aqi']

        iaqi = msg['iaqi']
        pm25 = None
        pm10 = None
        for item in iaqi:
            if item['p'] == 'pm25':
                # print(item)
                pm25 = item['v']
            elif item['p'] == 'pm10':
                # print(item)
                pm10 = item['v']
        # update_time = msg['time']['s']['cn']['time']
        update_time = 0 
        try :
            update_time = msg['time']['s']['en']['time']
        except:
            print('update_time error:', msg['time'], file=sys.stderr)
        
        result['city'] = city
        result['update_time'] = update_time
        result['aqi'] = aqi
        result['pm25'] = pm25
        result['pm10'] = pm10
        return True
    except:
        print(s, file=sys.stderr)
        return False

def main():
    print(datetime.datetime.now())
    # get json data from aqi
    for i in range(0,3):
        result = dict()
        ok = get_data(result)
        if ok:
            print(result['update_time'])
            print(result['city'])
            print(result['aqi'])
            print(result['pm25'])
            print(result['pm10'])
            break
        else:
            time.sleep(30)

if __name__ == '__main__':
    main()
