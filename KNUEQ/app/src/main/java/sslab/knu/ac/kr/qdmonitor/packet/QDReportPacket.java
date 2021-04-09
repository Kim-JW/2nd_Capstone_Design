package sslab.knu.ac.kr.qdmonitor.packet;

public class QDReportPacket extends QDPacket {
    QDReportPacket(byte[] d) {
        super(d);
    }

    public String getEventType() {
        switch(type) {
            case QDPacket.PACKET_TYPE_STEADY:
                return "Steady";
            case QDPacket.PACKET_TYPE_TRIGGER:
                return "Trigger";
            case QDPacket.PACKET_TYPE_PROCESSIJNG:
                return "Processing";
            case QDPacket.PACKET_TYPE_EQ:
                return "Earthquake level";
        }
        return "Not Earthquake";
    }
}
