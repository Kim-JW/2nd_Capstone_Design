import socket
from subprocess import call

def display_warning_image():
    call('gpicview earthquake.jpg')

def play_alarm_mp3():
    call('mplayer alarm.mp3', shell=True)

HOST = ''
PORT = 5008
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(1)
conn,addr = s.accept()
print('Connected by'), addr
while 1:
    data = conn.recv(1024)
    a = data.decode()
    print(a)
    if a == '1':
        display_warning_image()
        break
    if a == '2':
        play_alarm_mp3()
        break

    #if not data: break

    conn.sendall(data)
conn.close()