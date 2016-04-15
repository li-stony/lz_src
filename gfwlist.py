import http.client
import json
import urllib.request
import base64
import io

def get_list():
    url = 'https://raw.githubusercontent.com/gfwlist/gfwlist/master/gfwlist.txt'
    r1 = urllib.request.urlopen(url)
    data = r1.read()
    b64str = data.decode('utf-8')
    text = base64.b64decode(b64str).decode('utf-8')
    return text

if __name__ == '__main__':
    result = get_list()
    #print(result)
    f = open("gfwlist.txt", mode='w', encoding='utf-8')
    f.write(result)
    f.close()
    
    
