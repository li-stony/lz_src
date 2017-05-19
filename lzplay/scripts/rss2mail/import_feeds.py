#!/usr/bin/python3
import sys
from pymongo import MongoClient

def write_to(db, path):
    print(path)
    dbUrl = 'mongodb://'+db+':27017/'
    print(dbUrl)
    client = MongoClient(dbUrl)
    db = client.rss2mail

    file = open(path, 'r', encoding='utf-8')
    count =0;
    for line in file.readlines():
        if line.startswith('#'):
            continue
        else:
            count = count + 1
            line = line.replace('\n', '')
            print(line)
            #fmt = "{{_id:{0:d}, rss:{1}, title:{2}, last_update:{3:d}}}"
            #query = fmt.format(count, line, '', 0)
            #print(query)
            if True:
                db.feeds.insert({
                    '_id': count,
                    'title': '',
                    'rss': line,
                    'home': '',
                    'last_update': 0

                })
    print('import complete:', count)


if __name__ == '__main__':
    print(sys.argv)
    if len(sys.argv) > 2:
        path = sys.argv[1]
        db = sys.argv[2]
        write_to(db, path)
    else:
        print('please set the feeds file')
