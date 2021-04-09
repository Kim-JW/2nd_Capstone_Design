from bluepy.btle import Scanner, DefaultDelegate
from subprocess import call
import codecs, binascii

def display_warning_image():
    call( 'gpicview earthquake.jpg')

def play_alarm_mp3():
    call( 'mplayer alarm.mp3', shell=True)

def show_received_data( _adtype, _desc, _value, _device):
    print( "Raw data:", _device.rawData)
    if type( _device.rawData) is not str:
        # encoding binary data to string(?)
        binRawData = binascii.hexlify( _device.rawData)
        print( "-> Raw string:", binRawData)
    print( "Adtype: ", _device.getValueText(_adtype))
    print( "%s: %s\n" % (_desc, _value))

    # call subprocess via function when we get a ble message from some devices
    play_alarm_mp3()
    display_warning_image()

class ScanDelegate( DefaultDelegate):
    def __init__(self):
        DefaultDelegate.__init__(self)

    def handleDiscovery(self, dev, isNewDev, isNewData):
        if isNewDev:
            print( "Discovered device", dev.addr)
        elif isNewData:
            print( "Received new data from", dev.addr)
