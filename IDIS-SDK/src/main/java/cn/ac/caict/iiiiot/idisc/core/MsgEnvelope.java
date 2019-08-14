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
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;

public class MsgEnvelope {
	public byte protocolMajorVersion = MessageCommon.COMPATIBILITY_MAJOR_VERSION;// 主版本：协议格式可能会发生变化
	public byte protocolMinorVersion = MessageCommon.COMPATIBILITY_MINOR_VERSION;// 次版本：协议增加额外功能，不影响消息格式

	public byte suggestMajorProtocolVersion = MessageCommon.MAJOR_VERSION;
	public byte suggestMinorProtocolVersion = MessageCommon.MINOR_VERSION;

	public int sessionId = 0;// 多个连接共用一个sessionId
	public int requestId; // 一组请求与响应对应的一个标识
	public int messageSequenceNum = 0;//消息分片跟踪计数
	public int messageLength;//每个消息字节流总数目，不包括信封部分

	public boolean truncated = false;// 消息是否截断
	public boolean encrypted = false;// 消息是否加密，不包括信封
	public boolean compressed = false;// 消息是否回缩

	public MsgEnvelope() {
	}

	public String toString() {
		return "protocol=" + protocolMajorVersion + '.' + protocolMinorVersion + "; req="
				+ Integer.toHexString(requestId) + "h; msg=" + messageSequenceNum + "; len=" + messageLength;
	}
}
