package com.example.sslab.p2p_client;

public class Packet {
    //byte status;
    //byte Level;
    //byte Padding;
    byte[] status = new byte[1];
    byte[] Level = new byte[1];
    byte[] Padding = new byte[2];
    byte[] ANN = new byte[4];
    byte[] PGA = new byte[4];
    byte[] STA_LTA = new byte[4];
}
