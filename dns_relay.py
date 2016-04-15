# encoding=utf8

from dnslib import *
import socket
import threading
import signal
import time
import sys
import traceback
import struct

loglock = threading.Lock()
def lzlog(tag, *msg):
    with loglock:
        print(time.strftime('%m-%d %H:%M:%S'), tag, ':', *msg)


class DnsConfig:
    dns_client_ip = '127.0.0.1'
    listen_port = 53

    dns_ip = ['8.8.8.8', '8.8.4.4']
    

    china_dns_ip = ['202.106.196.115']
    
    dns_port = 53
        
        
    
class LzDns:
    tag = 'LzDns'
    config = None

    tcp_sock = None
    listen_sock = None

    reqDict = dict()

    running = True
    
    def __init__(self, config):
        self.config = config

    def create_client(self):
        lzlog(self.tag, 'creating client ...')
        sock = None
        for i in range(0, len(self.config.dns_ip)-1):
            try:
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                lzlog(self.tag, 'bind to:',(self.config.dns_client_ip, 0)) 
                sock.bind((self.config.dns_client_ip, 0))
                lzlog(self.tag,'connect to:', (self.config.dns_ip[i], 53))
                sock.connect((self.config.dns_ip[i], 53))
                self.tcp_sock = sock
                break
            except Exception as err:
                print(err)
                traceback.print_tb(sys.exc_info()[2])
        
        if sock == None:
            lzlog('CreateClient','can\'t connect any dns server')
        return  sock   

    def send_question_to_dns(self, buf):
        lzlog('Send Question', len(buf))
        while True:
            if self.tcp_sock == None:
                print('tcp_sock is None')
                break;
            try:
                ret = self.tcp_sock.send(buf)
                if ret <= 0:
                    lzlog('connection to dns closed')
                    break
                else:
                    lzlog('send to dns ok')
                    break;
                ret = self.tcp_sock.recv(2)
                data_size = ret[0]*256 + ret[1]
                ret = self.tcp_sock.recv(ret)
                self.recv_answer(ret)
            except Exception as err:
                print(err)
                traceback.print_tb(sys.exc_info()[2])
                self.tcp_sock = None

    def send_answer(self, buf, addr):
        try:
            tmp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            tmp.sendto(buf, addr)
            tmp.close()
        except Exception as err:
            lzlog(self.tag, err)
    
    def start(self):
        lzlog(self.tag, 'zl dns starting ...')
        while self.running:
            lzlog(self.tag, 'Dns Server Starting ...')
            self.listen_sock = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
            self.listen_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            self.listen_sock.bind(('127.0.0.1', 53))
            #
            self.tcp_sock = self.create_client()
            while True:
                try:
                    lzlog('UdpServer', 'server receiving ... ')
                    req = self.listen_sock.recvfrom(512)
                    question = DNSRecord.parse(req[0])
                    lzlog('UdpServer', 'received question:',question)
                    self.reqDict[question.header.id] = req[1]

                    data_size = len(req[0])
                    # a/b always return a float value
  
                    self.send_question_to_dns(struct.pack('>hp', len(req[0]), req[0]))
                    
                except Exception as err:
                    print(err)
                    traceback.print_tb(sys.exc_info()[2])
                    break;
                
        lzlog(self.tag,'zl dns proxy exit')
        
        
    def stop(self):
        self.running = False
        self.listen_sock.close()
        
        
    def recv_answer(buf):
        ans = DNSRecord.parse(buf)
        addr = reqDict.pop(ans.header.id, None)
        if addr != None:
            send_anwser(buf, addr)

  
config = DnsConfig()
dns = None

def sig_exit(signal, frame):
    print('received Ctrl-C')
    if dns is not None:
        dns.stop()

if __name__ == '__main__' :
    signal.signal(signal.SIGINT, sig_exit)
    
    if len(sys.argv) == 2 :
        config.dns_client_ip = sys.argv[1]
    if len(sys.argv) == 1:
        #config.dns_client_ip = '0.0.0.0'
        config.dns_client_ip = '192.168.31.221'
    dns = LzDns(config)
    dns.start()
