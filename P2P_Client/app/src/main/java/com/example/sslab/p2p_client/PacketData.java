package com.example.sslab.p2p_client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketData implements EqPacket {
    byte[] status = new byte[1];
    byte[] rawx = new byte[4];
    byte[] rawy = new byte[4];
    byte[] rawz = new byte[4];
    byte[] ts = new byte[8];

    public PacketData(byte[] b) {
        System.arraycopy(b, 0, status, 0, 1);
        System.arraycopy(b, 4, rawx, 0, 4);
        System.arraycopy(b, 8, rawy, 0, 4);
        System.arraycopy(b, 12, rawz, 0, 4);
        System.arraycopy(b, 16, ts, 0, 8);
    }

    public float[] getAcc() {
        float[] result = {0, 0, 0};
        result[0] = ByteBuffer.wrap(rawx).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        result[1] = ByteBuffer.wrap(rawy).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        result[2] = ByteBuffer.wrap(rawz).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return result;
    }

    public long getTimestamp() {
        return ByteBuffer.wrap(ts).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        float[] acc = getAcc();
        sb.append("X: ");
        sb.append(acc[0]);
        sb.append('\n');
        sb.append("Y: ");
        sb.append(acc[1]);
        sb.append('\n');
        sb.append("Z: ");
        sb.append(acc[2]);
        sb.append('\n');
        sb.append("TS: ");
        sb.append(getTimestamp());
        sb.append('\n');
        return sb.toString();
    }
}
