#!/usr/bin/python3

import sys
import re

p = re.compile('^\|\s+(\w+)\s+\|\s+(\S+)\s+\|\s+(\S+)\s+\|')

class Record:
    def __init__(self):
        self.date = '0000'
        self.value = 0.00
        self.mark = ''
        
    def __lt__(self,other):
        return self.value < other.value
    def __le__(self,other):
        return self.value <= other.value
    
    def __gt__ (self,other):
        return self.value > other.value
    def __ge__ (self,other):
        return self.value >= other.value

    def __eq__(self,other):
        return self.value == other.value
    
    def __nq__(self,other):
        return self.value != other.value
    
    def __str__ (self):
        return "{0} {1:10.2f} {2}".format(self.date, self.value, self.mark)
    def __repr__(self):
        return self.__str__()
    
def summary(records):
    print('-- sum --')
    sum = 0.0
    for r in records:
        sum = r.value + sum
        
    print(sum)
    
def most(records):
    print('-- most --')
    x = sorted(records, reverse=True)
    for i in range(0, 10):
        print(x[i])
    
def groups(records):
    print('-- groups --')
    d = dict()
    for r in records:
        if r.mark != None or len(r.mark) > 0:
            if r.mark not in d:
                d[r.mark] = 0.0
            d[r.mark] = d[r.mark]+r.value

    for key in d:
        print("{0:<10.2f} {1}".format(d[key], key))

    
def analyse (path):
    print(p)
    rs = list()
    f = open(path)
    running = False
    for line in f.readlines():
        line = line.rstrip('\n')
        # print(line)
        m = re.match(p, line)
        if m is None:
            continue
        
        record = Record()
        record.date = m.group(1)
        record.value = float(m.group(2))
        record.mark = m.group(3)
        rs.append(record)
    summary(rs)
    most(rs)
    groups(rs)

if __name__ == '__main__':
    if len(sys.argv) == 2:
        analyse(sys.argv[1])
    else:
        print(sys.argv[0], "target-file")
