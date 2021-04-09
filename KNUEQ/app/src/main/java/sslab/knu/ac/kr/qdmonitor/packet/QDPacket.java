package sslab.knu.ac.kr.qdmonitor.packet;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QDPacket implements Parcelable {
    public static final byte PACKET_TYPE_DATA = 4;
    public static final byte PACKET_TYPE_STEADY = (byte)128;
    public static final byte PACKET_TYPE_TRIGGER = 64;
    public static final byte PACKET_TYPE_PROCESSIJNG = 32;
    public static final byte PACKET_TYPE_EQ = 16;
    public static final byte PACKET_TYPE_NQ = 8;

    public byte type;
    public byte level;
    public float[] vals;
    public long ts;

    public QDPacket(byte[] d) {
        type = d[0];
        level = d[1];
        vals = new float[3];
        vals[0] = ByteBuffer.wrap(d, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        vals[1] = ByteBuffer.wrap(d, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        vals[2] = ByteBuffer.wrap(d, 12, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        ts = ByteBuffer.wrap(d, 16, 8).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public QDPacket(Parcel in) {
        type = in.readByte();
        level = in.readByte();
        in.readFloatArray(vals);
        ts = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(type);
        parcel.writeByte(level);
        parcel.writeFloatArray(vals);
        parcel.writeLong(ts);
    }

    public static final Parcelable.Creator<QDPacket> CREATOR = new Parcelable.Creator<QDPacket>() {
        public QDPacket createFromParcel(Parcel in) {
            return new QDPacket(in);
        }

        public QDPacket[] newArray(int size) {
            return new QDPacket[size];
        }
    };

    public static QDPacket QDPacketBuilder(byte[] d) {
        if(d[0] == PACKET_TYPE_DATA) {
            return new QDDataPacket(d);
        } else {
            return new QDReportPacket(d);
        }
    }
 }
