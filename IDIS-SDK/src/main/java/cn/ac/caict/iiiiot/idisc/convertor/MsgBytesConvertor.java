package cn.ac.caict.iiiiot.idisc.convertor;
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
import cn.ac.caict.iiiiot.idisc.core.AddValueRequest;
import cn.ac.caict.iiiiot.idisc.core.BaseMessage;
import cn.ac.caict.iiiiot.idisc.core.ChallengeAnswerRequest;
import cn.ac.caict.iiiiot.idisc.core.CreateIdentifierRequest;
import cn.ac.caict.iiiiot.idisc.core.DeleteIdentifierRequest;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.core.LoginIdisRequest;
import cn.ac.caict.iiiiot.idisc.core.LoginRequest;
import cn.ac.caict.iiiiot.idisc.core.ModifyValueRequest;
import cn.ac.caict.iiiiot.idisc.core.MsgEnvelope;
import cn.ac.caict.iiiiot.idisc.core.RemoveValueRequest;
import cn.ac.caict.iiiiot.idisc.core.ResolutionRequest;
import cn.ac.caict.iiiiot.idisc.core.SiteRequest;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;

public abstract class MsgBytesConvertor extends BaseConvertor {
	public static final byte[] messageConvertIntoBytes(BaseMessage msg) throws IdentifierException {
		byte[] msg_buf = null;
		if (msg.opCode == MessageCommon.OC_RESOLUTION)
			msg_buf = convertResolutionReqToBytes((ResolutionRequest) msg);
		else if (msg.opCode == MessageCommon.OC_LOGIN)
			msg_buf = convertLoginReqToBytes((LoginRequest) msg);
		else if (msg.opCode == MessageCommon.OC_LOGIN_IDIS)
			msg_buf = convertLoginIdisReqToBytes((LoginIdisRequest) msg);
		else if (msg.opCode == MessageCommon.OC_CREATE_IDENTIFIER)
			msg_buf = convertCreateIdentifierReqToBytes((CreateIdentifierRequest) msg);
		else if (msg.opCode == MessageCommon.OC_DELETE_IDENTIFIER)
			msg_buf = convertDeleteIdentifierReqToBytes((DeleteIdentifierRequest) msg);
		else if (msg.opCode == MessageCommon.OC_RESPONSE_TO_CHALLENGE)
			msg_buf = convertChallengeAnswerReqToBytes((ChallengeAnswerRequest) msg);
		else if (msg.opCode == MessageCommon.OC_ADD_VALUE)
			msg_buf = convertAddValueReqToBytes((AddValueRequest) msg);
		else if (msg.opCode == MessageCommon.OC_MODIFY_VALUE)
			msg_buf = convertModifyValueReqToBytes((ModifyValueRequest) msg);
		else if (msg.opCode == MessageCommon.OC_REMOVE_VALUE)
			msg_buf = convertRemoveValueReqToBytes((RemoveValueRequest) msg);
		else if(msg.opCode == MessageCommon.OC_GET_SITE){
			msg_buf = convertSiteReqToBytes((SiteRequest) msg);
		}
		else
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR, "未知操作: " + msg.opCode);
		if (msg_buf == null)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR,
					msg.opCode + "该请求操作没有实现将消息转换为字节!");
		return msg_buf;
	}
	public static void envelopeConvertInfoBytes(MsgEnvelope msgEnv, byte[] udpPkt) throws IdentifierException {
		//共20字节
		udpPkt[0] = msgEnv.protocolMajorVersion;
		udpPkt[1] = msgEnv.protocolMinorVersion;
		// 压缩、加密、截断、主要协议版本
		byte byteCompressed = msgEnv.compressed ? Common.ENV_FLAG_COMPRESSED : 0;
		byte byteEncrypted = msgEnv.encrypted ? Common.ENV_FLAG_ENCRYPTED : 0;
		byte byteTruncated = msgEnv.truncated ? Common.ENV_FLAG_TRUNCATED : 0;
		udpPkt[2] = (byte) (byteCompressed | byteEncrypted | byteTruncated | msgEnv.suggestMajorProtocolVersion);
		// 建议次要协议版本
		udpPkt[3] = msgEnv.suggestMinorProtocolVersion;
		write4Bytes(udpPkt, 4, msgEnv.sessionId); // bytes 4,5,6,7
		write4Bytes(udpPkt, 8, msgEnv.requestId); // bytes 8,9,10,11
		write4Bytes(udpPkt, 12, msgEnv.messageSequenceNum); // bytes 12,13,14,15
		write4Bytes(udpPkt, 16, msgEnv.messageLength); // bytes 16,17,18,19
	}
	public static int convertIdentifierValueToByte(byte[] buf, int offset, IdentifierValue value) {
		if(value == null)
			return 0;
		int origOffset = offset;
		write4Bytes(buf, offset, value.index);
		offset += Common.FOUR_SIZE;
		write4Bytes(buf, offset, (int) value.timestamp);
		offset += Common.FOUR_SIZE;
		buf[offset++] = value.ttlType;
		write4Bytes(buf, offset, value.ttl);
		offset += Common.FOUR_SIZE;
		byte byteAdminRead = value.bAdminRead ? Common.PERM_ADMIN_READ : 0;
		byte byteAdminWrite = value.bAdminWrite ? Common.PERM_ADMIN_WRITE : 0;
		byte bytePublicRead = value.bPublicRead ? Common.PERM_PUBLIC_READ : 0;
		byte bytePublicWrite = value.bPublicWrite ? Common.PERM_PUBLIC_WRITE : 0;
		buf[offset++] = (byte) (byteAdminRead | byteAdminWrite | bytePublicRead | bytePublicWrite);
		offset += writeByteArray(buf, offset, value.type, 0, value.type.length);
		offset += writeByteArray(buf, offset, value.data, 0, value.data.length);
		if (value.references != null) {
			offset += write4Bytes(buf, offset, value.references.length);
			for (int i = 0; i < value.references.length; i++) {
				offset += writeByteArray(buf, offset, value.references[i].identifier);
				offset += write4Bytes(buf, offset, value.references[i].index);
			}
		} else {
			offset += write4Bytes(buf, offset, 0);
		}
		return offset - origOffset;
	}
	
	public static byte[] convertIdentifierValueToByte(IdentifierValue value) {
		byte[] buf = new byte[calcStorageSize(value)];
		convertIdentifierValueToByte(buf, 0, value);
		return buf;
	}
	
	public static byte[] convertSiteReqToBytes(SiteRequest req) {
		// TODO Auto-generated method stub
		int idSize = req.identifier.length;
		int idLen = Common.FOUR_SIZE;
		byte[] msg = new byte[Common.MESSAGE_HEADER_SIZE + idSize + idLen];
		int offset = writeHeader(req, msg, idSize + idLen);
		writeByteArray(msg,offset,req.identifier);
		return msg;
	}
	//////////////////////////////////////////////// private-founctions//////////////////////////////////////////////////////
	private static byte[] convertResolutionReqToBytes(ResolutionRequest req) {
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE
				+ ((req.requestedIndexes == null) ? 0 : req.requestedIndexes.length * Common.FOUR_SIZE)
				+ Common.FOUR_SIZE;
		if (req.requestedTypes != null) {
			for (int i = 0; i < req.requestedTypes.length; i++) {
				bodyLen += (req.requestedTypes[i].length + Common.FOUR_SIZE);
			}
		}
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		writeHeader(req, msg, bodyLen);
		int pos = Common.MESSAGE_HEADER_SIZE;
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		pos += writeIntArray(msg, pos, req.requestedIndexes);
		pos += writeByteArrayArray(msg, pos, req.requestedTypes);
		return msg;
	}

	private static byte[] convertLoginReqToBytes(LoginRequest req) {
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE
				+ ((req.requestedIndexes == null) ? 0 : req.requestedIndexes.length * Common.FOUR_SIZE)
				+ Common.FOUR_SIZE;
		if (req.requestedTypes != null) {
			for (int i = 0; i < req.requestedTypes.length; i++) {
				bodyLen += (req.requestedTypes[i].length + Common.FOUR_SIZE);
			}
		}
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		writeHeader(req, msg, bodyLen);
		int pos = Common.MESSAGE_HEADER_SIZE;
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		pos += writeIntArray(msg, pos, req.requestedIndexes);
		pos += writeByteArrayArray(msg, pos, req.requestedTypes);
		return msg;
	}

	private static byte[] convertRemoveValueReqToBytes(RemoveValueRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE
				+ req.indexes.length * Common.FOUR_SIZE;
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		int pos = writeHeader(req, msg, bodyLen);
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		pos += writeIntArray(msg, pos, req.indexes);
		return msg;
	}

	private static byte[] convertModifyValueReqToBytes(ModifyValueRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE;
		for (int i = 0; i < req.values.length; i++) {
			bodyLen += calcStorageSize(req.values[i]);
		}
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		int pos = writeHeader(req, msg, bodyLen);
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		pos += write4Bytes(msg, pos, req.values.length);
		for (int i = 0; i < req.values.length; i++)
			pos += convertIdentifierValueToByte(msg, pos, req.values[i]);
		return msg;
	}

	private static byte[] convertAddValueReqToBytes(AddValueRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE;

		for (int i = 0; i < req.values.length; i++)
			bodyLen += calcStorageSize(req.values[i]);
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		int pos = writeHeader(req, msg, bodyLen);
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		pos += write4Bytes(msg, pos, req.values.length);
		for (int i = 0; i < req.values.length; i++)
			pos += convertIdentifierValueToByte(msg, pos, req.values[i]);
		return msg;
	}

	private static byte[] convertChallengeAnswerReqToBytes(ChallengeAnswerRequest req) {
		// TODO Auto-generated method stub
		byte[] msg = new byte[Common.MESSAGE_HEADER_SIZE + Common.FOUR_SIZE + req.authType.length + Common.FOUR_SIZE
				+ req.userIdIdentifier.length + Common.FOUR_SIZE + Common.FOUR_SIZE + req.signedResponse.length];
		int offset = writeHeader(req, msg, msg.length - Common.MESSAGE_HEADER_SIZE);
		offset += writeByteArray(msg, offset, req.authType, 0, req.authType.length);
		offset += writeByteArray(msg, offset, req.userIdIdentifier, 0, req.userIdIdentifier.length);
		offset += write4Bytes(msg, offset, req.userIdIndex);
		offset += writeByteArray(msg, offset, req.signedResponse, 0, req.signedResponse.length);
		return msg;
	}

	private static byte[] convertDeleteIdentifierReqToBytes(DeleteIdentifierRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = Common.FOUR_SIZE + req.identifier.length;
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		int loc = writeHeader(req, msg, bodyLen);
		loc += writeByteArray(msg, loc, req.identifier, 0, req.identifier.length);
		return msg;
	}

	private static byte[] convertCreateIdentifierReqToBytes(CreateIdentifierRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = Common.FOUR_SIZE + req.identifier.length + Common.FOUR_SIZE;
		if (req.values != null && req.values.length > 0) {
			for (int i = 0; i < req.values.length; i++)
				bodyLen += calcStorageSize(req.values[i]);
		}
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		int pos = writeHeader(req, msg, bodyLen);
		pos += writeByteArray(msg, pos, req.identifier, 0, req.identifier.length);
		if (req.values != null && req.values.length > 0) {
			pos += write4Bytes(msg, pos, req.values.length);
			for (int i = 0; i < req.values.length; i++) {
				pos += convertIdentifierValueToByte(msg, pos, req.values[i]);
			}
		} else {
			pos += write4Bytes(msg, pos, 0);
		}
		return msg;
	}

	private static byte[] convertLoginIdisReqToBytes(LoginIdisRequest req) {
		// TODO Auto-generated method stub
		int bodyLen = req.identifier.length + Common.FOUR_SIZE * 2;
		byte[] msg = new byte[bodyLen + Common.MESSAGE_HEADER_SIZE];
		writeHeader(req, msg, bodyLen);
		int loc = Common.MESSAGE_HEADER_SIZE;
		loc += writeByteArray(msg, loc, req.identifier, 0, req.identifier.length);
		loc += write4Bytes(msg, loc, req.requestedIndexes);
		return msg;
	}

	private static final int writeHeader(BaseMessage msg, byte[] buf, int bodyLen) {
		int offset = 0;
		offset += write4Bytes(buf, offset, msg.opCode);
		offset += write4Bytes(buf, offset, msg.responseCode);
		int flags = 0;
		if (msg.bAuthoritative)
			flags |= Common.FLAG_1ST_MSG_AUTH;
		if (msg.bCertify)
			flags |= Common.FLAG_2ND_MSG_CERT;
		if (msg.bEncrypt)
			flags |= Common.FLAG_3RD_MSG_ENCR;
		if (msg.bRecursive)
			flags |= Common.FLAG_4TH_MSG_RECU;
		if (msg.bCacheCertify)
			flags |= Common.FLAG_5TH_MSG_CACR;
		if (msg.continuous)
			flags |= Common.FLAG_6TH_MSG_CONT;
		if (msg.bKeepAlive)
			flags |= Common.FLAG_7TH_MSG_KPAL;
		if (msg.ignoreRestrictedValues)
			flags |= Common.FLAG_8TH_MSG_PUBL;
		if (msg.returnRequestDigest)
			flags |= Common.FLAG_9TH_MSG_RRDG;
		if (msg.overwriteWhenExists)
			flags |= Common.FLAG_10TH_MSG_OVRW;
		if (msg.mintNewSuffix)
			flags |= Common.FLAG_11TH_MSG_MINT;
		if (msg.doNotRefer)
			flags |= Common.FLAG_12TH_MSG_DNRF;
		if (msg.trustedQuery)
			flags |= Common.FLAG_31ST_MSG_TRUSTED_QUERY;
		offset += write4Bytes(buf, offset, flags);
		offset += write2Bytes(buf, offset, msg.siteInfoSerial);
		buf[offset++] = (byte) msg.recursionCount;
		offset++;
		offset += write4Bytes(buf, offset, msg.expiration);
		offset += write4Bytes(buf, offset, bodyLen);
		return Common.MESSAGE_HEADER_SIZE;
	}
}
