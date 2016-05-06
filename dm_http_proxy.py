#! /usr/bin/python3

import sys
from dnslib import *
import socket
import select
from urllib.parse import urlparse
import time
import signal
import re

vpn_ip = "192.168.31.221"
vpn_dns = "192.168.31.1"


def parse_url(url):

    scheme = 'http'
    host = ''
    port = 80
    colon1 = url.find(':')
    if colon1 == -1:
        # no :
        slash = url.find('/')
        if slash == -1 :
            slash = len(url)
        host = url[0:slash]
    else:
        # has :
        if colon1 < 5:
            scheme = url[0:colon1]
            colon2 = url.find(':', colon1+1)

            if colon2 > 0:
                slash = url.find('/', colon1+3)
                if slash < colon2 :
                    # this : is in path
                    host = url[colon1+3:slash]
                else :
                    # this : is before port
                    host = url[colon1+3:colon2]
                    port = int(url[colon2+1:slash])
            else:
                slash = url.find('/', colon1+3)
                if slash == -1:
                    host = url[colon1+3:len(url)]
                else:
                    host = url[colon1+3:slash]
        else:
            # has port :, no scheme :
            slash = url.find('/', colon1+1)
            if slash == -1:
                port = int(url[colon1+1:len(url)])
                host = url[0:colon1]
            else:
                host = url[0:colon1]
                port = int(url[colon1+1:slash])

    
    
    return (scheme, host, port)
    
        

class LzDns(object):
    lastClear = 0
    cache = dict()
    local_ip = ''
    dns_ip = ''
    def __init__(self, ip, dns):
        super(object, self).__init__()
        self.local_ip = ip
        self.dns_ip = dns
    def query_addr(self, name):
        print('query:',name)
        pat = re.compile("\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}")
        if pat.match(name):
            print(name, 'is ip address')
            return name
        now = time.time()
        if now - self.lastClear > 7200:
            print('>>> clean dns cache')
            self.cache.clear()
            self.lastClear = now
            
        addr = ''
        if name in self.cache:
            addr = self.cache[name]
            print('>>> shoot dns cache')
        else:
            try :
                d = DNSRecord(q=DNSQuestion(name,QTYPE.A))
                sock = socket.socket(socket.AF_INET, type=socket.SOCK_DGRAM)
                sock.settimeout(5)
                sock.bind((self.local_ip, 0))
                sock.sendto(d.pack(), (self.dns_ip, 53))
                data = sock.recv(512)
                answer = DNSRecord.parse(data)
                print(answer)
                for d in answer.rr:
                    if d.rtype == QTYPE.A:
                        addr = repr(d.rdata)
                
                self.cache[name] = addr
            except socket.timeout as err:
                print('query dns', err)
            
        print('>>> got address:' , addr)
        return addr

class LzProxy (object):
    dnsObj = None
    vpn_ip = ''
    
    listen_port = 1080
    
    server_sock = None
    in_map = dict()
    out_map = dict()

    out_data = dict()
    
    running = True

    rlist = list()
    wlist = list()
    xlist = list()
    
    def start(self, ip, dns='192.168.31.1'):
        print('start ...')
        self.dnsObj = LzDns(ip, dns)
        self.vpn_ip = ip
        
        self.server_sock = socket.socket()
        self.server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.server_sock.bind(('0.0.0.0', self.listen_port))
        self.server_sock.listen(32)
        print('listening ...')
        self.rlist.append(self.server_sock)
        self.running = True
        while self.running:
            #print(self.rlist)
            #print(self.wlist)
            #print(self.xlist)
            print('r,w,x,d,c=',len(self.rlist), len(self.wlist), len(self.xlist), len(self.out_data), len(self.dnsObj.cache))
            result = select.select(self.rlist,self.wlist,self.xlist, 5)
            for s in result[0] :
                if s == self.server_sock:
                    client = s.accept()
                    print('accept:', client[1])
                    self.create_pair(client[0])
                else:
                   self.forward_data(s) 
                        
            for s in result[1]:
                self.send_connect_data(s)
                
            for s in result[2]:
                if s in self.out_data:
                    del self.out_data[s]
                self.xlist.remove(s)
                self.close_paired_sock(s)
                
        print('stopped')
            
    def create_pair(self, client):
        data = client.recv(1024)
        if len(data) == 0:
            client.close()
            return
        index = data.find(b'\r\n')
        head = data[0:index]
        cmd = str(head, 'utf-8')
        print('recv:', cmd)
        tokens = cmd.split(' ')
        url = tokens[1]
        u = parse_url(url)
        print(u)
        ip = self.dnsObj.query_addr(u[1])
        if ip == '' or ip == None:
            client.close()
            return
        
        outgoing = socket.socket()
        # socket.SO_BINDTODEVICE
        # outgoing.setsockopt(socket.SOL_SOCKET, 25, struct.pack('7s', b'wlp4s0'))
        #print('bind to:', self.vpn_ip)
        err = outgoing.bind((self.vpn_ip, 0))
        print('bind result:', err)
        #outgoing.setsockopt(socket.SOL_SOCKET, socket.SOCK_NONBLOCK , 1)
        outgoing.setblocking(0)
        
        outgoing.connect_ex((ip, u[2]))
        print('connecting...', outgoing.getsockname())
        self.in_map[client] = outgoing
        self.out_map[outgoing] = client
        
        self.rlist.append(client)
        self.rlist.append(outgoing)
        self.wlist.append(outgoing)

        # if connect cmd, not send data to dest host
        if tokens[0].lower() != 'connect':
            self.out_data[outgoing] = data
        
             
        

    def forward_data(self, s):
        s2 = self.get_paired_sock(s)
        try :
            data = s.recv(1024)
            if len(data) > 0 :
                print(len(data), s.getpeername(), '=>', s2.getpeername())
                s2.send(data)
            else:
                self.close_paired_sock(s)
        except :
            self.close_paired_sock(s)
            return
        
        
            
    def send_connect_data(self, s):
        
        try:
            s2 = self.get_paired_sock(s)
            print('connected:', s2.getpeername(), '<+>', s.getpeername())
            s.setblocking(True)
            self.wlist.remove(s)
            if s in self.out_data:
                print('forward cmd data')
                data = self.out_data[s]
                del self.out_data[s]
                s.send(data)
            else:
                print('connect cmd. send response')
                res = 'HTTP/1.1 200 Connection established\r\n\r\n'
                data = res.encode('utf-8')
                print(data)
                s2.send(data)
        except OSError as err:
            print(err)
            print(s.__class__)
        except:
            self.close_paired_sock(s)
        

    def get_paired_sock(self, s):
        if s in self.in_map:
            return self.in_map[s]
        elif s in self.out_map:
            return self.out_map[s]
        else:
            return None

    def close_paired_sock(self, s):
        
        try:
            if (s not in self.in_map) and ( s not in self.out_map):
                print("closed already")
                return;
                      
            
            s2 = self.get_paired_sock(s)
            if s in self.rlist:
                self.rlist.remove(s)
            if s in self.wlist:
                self.wlist.remove(s)
                
            if s2 in self.rlist:
                self.rlist.remove(s2)
            if s2 in self.wlist:
                self.wlist.remove(s2)
                
            if s in self.in_map:
                del self.out_map[self.in_map[s]]
                del self.in_map[s]
            elif s in self.out_map:
                del self.in_map[self.out_map[s]]
                del self.out_map[s]
            print('close sock:', s.getsockname(),':' , s.getpeername())
            print('close sock:', s2.getsockname(),':' , s2.getpeername())
            s.close()
            s2.close()
        except OSError as err:
            print(err)
            print(s.__class__)
        except :
            print ('except when close paired')
        
        
    def stop(self):
        print('stop ... ')
        self.running = False
        if(self.server_sock != None) :
            self.server_sock.close()
    
global proxy
def sig_exit(signum, frame):
    if proxy != None:
        proxy.stop()

if __name__ == '__main__':

    signal.signal(signal.SIGINT, sig_exit)
    
    #d = LzDns(vpn_ip, vpn_dns)
    #d.query_addr('www.baidu.com')
##    u = parse_url('http://192.168.2.88:88/zentao/misc-ping.html')
##    print(u)

##    u = urlparse('//www.baidu.com:443')
##    print(u)
##    print(u.port)

    if len(sys.argv) == 3:
        vpn_ip = sys.argv[1]
        vpn_dns = sys.argv[2]
        print(sys.argv[0], sys.argv[1], sys.argv[2])
    else:
        print(sys.argv[0])
    proxy = LzProxy()
    proxy.start(vpn_ip, dns = vpn_dns)
    
    
