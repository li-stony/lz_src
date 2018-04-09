import mailme
import datetime
import json
import urllib
import urllib.request
import re

def get_data():
    # beijing url
    url = 'https://api.waqi.info/api/feed/@1451/obs.cn.json'
    req = urllib.request.Request(
        url, 
        data=None, 
        headers={
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.47 Safari/537.36'
        }
    )

    res = urllib.request.urlopen(req, timeout=60)
    body = res.read()
    s = body.decode('utf8')
    json_data = json.loads(s)
    out = json.dumps(json_data, indent=4)
    # print(out)
    # city
    obs = json_data['rxs']['obs']
    msg = obs['msg']
    city = msg['city']['name']
    city_url = msg['city']['url']
    aqi = msg['aqi']

    iaqi = msg['iaqi']
    pm25 = None
    pm10 = None
    for item in iaqi:
        if item['p'] == 'pm25':
            print(item)
            pm25 = item['v']
        elif item['p'] == 'pm10':
            print(item)
            pm10 = item['v']
    # update_time = msg['time']['s']['cn']['time']
    update_time = msg['time']['s']['zh-CN']['time']
    # collection
    title = '{} {} AQI {}'.format(city, update_time, aqi)
    body = '{} <br>\nPM2.5: {}<br>\nPM10: {}<br>\n'.format(title, pm25, pm10)

    return title,body


def main():
    print(datetime.datetime.now(), 'start aqi')
    # get json data from aqi
    title,body = get_data()
    print(title)
    print(body)
    print(datetime.datetime.now(), 'end aqi')
    # mail it
    mailme.sendmail(title, body)

if __name__ == '__main__':
    main()