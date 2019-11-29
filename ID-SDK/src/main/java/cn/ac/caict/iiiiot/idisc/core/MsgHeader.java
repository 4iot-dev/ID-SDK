package cn.ac.caict.iiiiot.idisc.core;
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *© COPYRIGHT 2019 Corporation for Institute of Industrial Internet & Internet of Things (IIIIT);
 *                      All rights reserved. 
 * http://www.caict.ac.cn  
 * https://www.citln.cn/
 */
import cn.ac.caict.iiiiot.idisc.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.idisc.utils.Common;

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
