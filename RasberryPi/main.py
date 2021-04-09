from bluepy.btle import Scanner, DefaultDelegate
import scan

# search time(sec)
SEARCH_TIME = 10.0

if __name__ == '__main__':
    SCANNER = Scanner().withDelegate( scan.ScanDelegate())
    DEVICES = SCANNER.scan( SEARCH_TIME)

    for device in DEVICES:
        print( "Device %s [%s], RSSI=%d dB" % (device.addr, device.addrType, device.rssi))
        for (adtype, desc, value) in device.getScanData():
            scan.show_received_data( adtype, desc, value, device)    
