package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.id.client.utils.Common;

/**
 * 消息头：占24字节，8个字段
 */
public class MsgHeader {
	public static final int RESERVED = 1;
	public int opCode;
	public int responseCode;
	public int opFlags;
	public int serialNumber;
	public short recursionCount;
	public int exprTime;
	public int bodyLen;
	public int bodyOffset;

	public MsgHeader(byte[] msg, int offset) {
		int pos = offset;
		opCode = BytesMsgConvertor.read4Bytes(msg, pos);
		pos += Common.FOUR_SIZE;

		responseCode = BytesMsgConvertor.read4Bytes(msg, pos);
		pos += Common.FOUR_SIZE;

		opFlags = BytesMsgConvertor.read4Bytes(msg, pos);
		pos += Common.FOUR_SIZE;

		int serialNumber = BytesMsgConvertor.read2Bytes(msg, pos);
		pos += Common.TWO_SIZE;

		recursionCount = msg[pos++];

		pos += RESERVED; 

		exprTime = BytesMsgConvertor.read4Bytes(msg, pos);
		pos += Common.FOUR_SIZE;

		bodyLen = BytesMsgConvertor.read4Bytes(msg, pos);
		pos += Common.FOUR_SIZE;
		bodyOffset = pos;
	}
}
