package com.vm.shadowsocks.tcpip;


public class TCPHeader {

    public static final int FIN = 1;
    public static final int SYN = 2;
    public static final int RST = 4;
    public static final int PSH = 8;
    public static final int ACK = 16;
    public static final int URG = 32;

    static final short offset_src_port = 0; // 16位源端口
    static final short offset_dest_port = 2; // 16位目的端口
    static final int offset_seq = 4; // 32位序列号
    static final int offset_ack = 8; // 32位确认号
    static final byte offset_lenres = 12; // 4位首部长度/4位保留字
    static final byte offset_flag = 13; // 6位标志位
    static final short offset_win = 14; // 16位窗口大小
    static final short offset_crc = 16; // 16位校验和
    static final short offset_urp = 18; // 16位紧急数据偏移量

    public byte[] m_Data;
    public int m_Offset;

    public TCPHeader(byte[] data, int offset) {
        this.m_Data = data;
        this.m_Offset = offset;
    }

    public int getHeaderLength() {
        int lenres = m_Data[m_Offset + offset_lenres] & 0xFF;
        return (lenres >> 4) * 4;
    }

    public short getSourcePort() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_src_port);
    }

    public void setSourcePort(short value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_src_port, value);
    }

    public short getDestinationPort() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_dest_port);
    }

    public void setDestinationPort(short value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_dest_port, value);
    }

    public byte getFlags() {
        return m_Data[m_Offset + offset_flag];
    }

    public short getCrc() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_crc);
    }

    public void setCrc(short value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_crc, value);
    }

    public int getSeqID() {
        return CommonMethods.readInt(m_Data, m_Offset + offset_seq);
    }

    public int getAckID() {
        return CommonMethods.readInt(m_Data, m_Offset + offset_ack);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format("%s%s%s%s%s%s%d->%d %s:%s",
                (getFlags() & SYN) == SYN ? "SYN " : "",
                (getFlags() & ACK) == ACK ? "ACK " : "",
                (getFlags() & PSH) == PSH ? "PSH " : "",
                (getFlags() & RST) == RST ? "RST " : "",
                (getFlags() & FIN) == FIN ? "FIN " : "",
                (getFlags() & URG) == URG ? "URG " : "",
                getSourcePort() & 0xFFFF,
                getDestinationPort() & 0xFFFF,
                getSeqID(),
                getAckID());
    }
}
