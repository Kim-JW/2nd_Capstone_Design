package com.example.sslab.p2p_client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketReport implements EqPacket {
    //byte status;
    //byte Level;
    //byte Padding;
    byte[] status = new byte[1];
    byte[] Level = new byte[1];
    byte[] Padding = new byte[2];
    byte[] ANN = new byte[4];
    byte[] PGA = new byte[4];
    byte[] STA_LTA = new byte[4];
    int status_code;

    public static final String CODE_TO_STRING[] = {
            "",
            "",
            "",
            "No Earthquake",
            "Earthquake detected",
            "In Process state",
            "In Trigger state",
            "In Steady state",
    };

    public PacketReport(byte[] b, int sc) {
        System.arraycopy(b,0,status,0,1);
        System.arraycopy(b,1,Level,0,1);
        System.arraycopy(b,4,ANN,0,4);
        System.arraycopy(b,8,PGA,0,4);
        System.arraycopy(b,12,STA_LTA,0,4);
        status_code = sc;
    }

    public String getStatus() {
        return CODE_TO_STRING[status_code];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(status_code == 4) {
            sb.append("Level: ");
            sb.append(Level);
            sb.append('\n');
        }
        sb.append("ANN: ");
        sb.append(toFloat(ANN));
        sb.append('\n');
        sb.append("PGA: ");
        sb.append(toFloat(PGA));
        sb.append('\n');

        return sb.toString();
    }

    private float toFloat(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
}
