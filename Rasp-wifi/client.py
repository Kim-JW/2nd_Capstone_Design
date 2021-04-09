import socket

HOST = '192.168.0.4'
PORT = 5008
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST,PORT))
while 1:
    number = input("enter the number: ")
    s.sendall(number.encode('utf-8'))
    #data = s.recv(1024)
s.close()
#print ('Received',repr(data))