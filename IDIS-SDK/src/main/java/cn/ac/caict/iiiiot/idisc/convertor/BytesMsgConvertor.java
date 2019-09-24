package cn.ac.caict.iiiiot.idisc.convertor;
import cn.ac.caict.iiiiot.idisc.core.Attribute;
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
import cn.ac.caict.iiiiot.idisc.core.BaseMessage;
import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.ChallengeResponse;
import cn.ac.caict.iiiiot.idisc.core.CreateIdentifierResponse;
import cn.ac.caict.iiiiot.idisc.core.ErrorResponse;
import cn.ac.caict.iiiiot.idisc.core.GenericResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.core.IdisCommunicationItems;
import cn.ac.caict.iiiiot.idisc.core.LoginIdisResponse;
import cn.ac.caict.iiiiot.idisc.core.MsgEnvelope;
import cn.ac.caict.iiiiot.idisc.core.MsgHeader;
import cn.ac.caict.iiiiot.idisc.core.ResolutionResponse;
import cn.ac.caict.iiiiot.idisc.core.ServerInfo;
import cn.ac.caict.iiiiot.idisc.core.ServiceReferralResponse;
import cn.ac.caict.iiiiot.idisc.core.SiteInfo;
import cn.ac.caict.iiiiot.idisc.core.SiteResponse;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.ValueReference;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;

public abstract class BytesMsgConvertor extends BaseConvertor{

	public static final BaseMessage bytesConvertInfoMessage(byte[] msg, int offset, MsgEnvelope envelope)
			throws IdentifierException {
		MsgHeader msgHeader = new MsgHeader(msg, offset);
		int bodyOffset = msgHeader.bodyOffset;
		int pos = bodyOffset;

		BaseMessage message = null;
		int bodyOffsetBeforeRD = bodyOffset;
		byte requestDigest[] = null;
		byte rdHashType = 0;
		if (hasDigestInMsgHeader(msgHeader)) {
			rdHashType = msg[bodyOffset++];
			if(rdHashType == Common.HASH_CODE_SHA256){
				requestDigest = new byte[Common.SHA256_DIGEST_SIZE];
				System.arraycopy(msg, bodyOffset, requestDigest, 0, Common.SHA256_DIGEST_SIZE);
				bodyOffset += Common.SHA256_DIGEST_SIZE;
			} else if(rdHashType == Common.HASH_CODE_SHA1){
				requestDigest = new byte[Common.SHA1_DIGEST_SIZE];
				System.arraycopy(msg, bodyOffset, requestDigest, 0, Common.SHA1_DIGEST_SIZE);
				bodyOffset += Common.SHA1_DIGEST_SIZE;
			} else if(rdHashType == Common.HASH_CODE_MD5){
				requestDigest = new byte[Common.MD5_DIGEST_SIZE];
				System.arraycopy(msg, bodyOffset, requestDigest, 0, Common.MD5_DIGEST_SIZE);
				bodyOffset += Common.MD5_DIGEST_SIZE;
			} else if(rdHashType == Common.HASH_CODE_MD5_OLD_FORMAT){
				bodyOffset--;
				requestDigest = readByteArray(msg, bodyOffset);
				bodyOffset += Common.FOUR_SIZE + requestDigest.length;
			}
			else {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
						"不识别的哈希类型: " + ((int) rdHashType));
			}
		}
		int bodyLenAfterRD = msgHeader.bodyLen - (bodyOffset - bodyOffsetBeforeRD);
		
		if(msgHeader.responseCode == MessageCommon.RC_SUCCESS){
			if(msgHeader.opCode == MessageCommon.OC_RESOLUTION)
				message = convertBytesToResolutionResponse(msg, bodyOffset, envelope);
			else if(msgHeader.opCode == MessageCommon.OC_LOGIN)
				message = convertBytesToResolutionResponse(msg, bodyOffset, envelope);
			else if(msgHeader.opCode == MessageCommon.OC_LOGIN_IDIS)
				message = convertBytesToLoginIdisResponse(msg, bodyOffset, envelope);
			else if(msgHeader.opCode == MessageCommon.OC_CREATE_IDENTIFIER)
				message = convertBytesToCreateIdentifierResponse(msg, bodyOffset, envelope, bodyLenAfterRD);
			else if(msgHeader.opCode == MessageCommon.OC_DELETE_IDENTIFIER || msgHeader.opCode == MessageCommon.OC_ADD_VALUE ||
					msgHeader.opCode == MessageCommon.OC_MODIFY_VALUE || msgHeader.opCode == MessageCommon.OC_REMOVE_VALUE)
				message = convertBytesToGenericResponse(msg, bodyOffset, envelope);
			else if(msgHeader.opCode == MessageCommon.OC_GET_SITE){
				message = convertBytesToSiteResponse(msg,bodyOffset,bodyLenAfterRD,envelope);
			}
			else
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR,"未知操作码: " + msgHeader.opCode);
				
		} else if (msgHeader.responseCode == MessageCommon.RC_SERVICE_REFERRAL || msgHeader.responseCode == MessageCommon.RC_PREFIX_REFERRAL){
			message = convertBytesToServiceReferralResponse(msgHeader.responseCode, msg, bodyOffset, bodyOffset + bodyLenAfterRD, envelope);
		} else if((msgHeader.responseCode == MessageCommon.RC_ERROR) || (msgHeader.responseCode == MessageCommon.RC_INVALID_ADMIN) ||
				  (msgHeader.responseCode == MessageCommon.RC_INSUFFICIENT_PERMISSIONS) || (msgHeader.responseCode == MessageCommon.RC_AUTHENTICATION_FAILED ||
				  (msgHeader.responseCode == MessageCommon.RC_INVALID_CREDENTIAL) || (msgHeader.responseCode == MessageCommon.RC_AUTHEN_TIMEOUT) ||
				  (msgHeader.responseCode == MessageCommon.RC_VALUES_NOT_FOUND) || (msgHeader.responseCode == MessageCommon.RC_IDENTIFIER_NOT_FOUND) ||
				  (msgHeader.responseCode == MessageCommon.RC_IDENTIFIER_ALREADY_EXISTS) || (msgHeader.responseCode == MessageCommon.RC_VALUE_ALREADY_EXISTS) ||
				  (msgHeader.responseCode == MessageCommon.RC_INVALID_VALUE) || (msgHeader.responseCode == MessageCommon.RC_SERVER_TOO_BUSY) ||
				  (msgHeader.responseCode == MessageCommon.RC_PROTOCOL_ERROR) || (msgHeader.responseCode == MessageCommon.RC_INVALID_IDENTIFIER) || 
				  (msgHeader.responseCode == MessageCommon.RC_RELOGIN) || (msgHeader.responseCode == MessageCommon.RC_LOGIN_FIRST))){
			message = convertBytesToErrorResponse(msg, bodyOffset, envelope.messageLength + offset, envelope);
		} else if (msgHeader.responseCode == MessageCommon.RC_AUTHENTICATION_NEEDED){
			message = convertBytesToChallengeResponse(msg, bodyOffset, msgHeader.opCode, envelope);
		} else {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR,
					"未知响应码: " + msgHeader.responseCode);
		}

		if (requestDigest != null) {
			message.requestDigest = requestDigest;
			message.rdHashType = rdHashType;
		}

		if (message == null) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"消息格式错误: opCode=" + msgHeader.opCode + "; responseCode=" + msgHeader.responseCode);
		}

		message.messageBody = new byte[Common.MESSAGE_HEADER_SIZE + msgHeader.bodyLen];
		System.arraycopy(msg, offset, message.messageBody, 0, Common.MESSAGE_HEADER_SIZE + msgHeader.bodyLen);

		populateMsgWithEnvelopAndOpFlags(message,msgHeader,envelope);
		pos += msgHeader.bodyLen;
		if (offset + envelope.messageLength >= pos + Common.FOUR_SIZE) {
			int signatureLength = read4Bytes(msg, pos);
			pos += Common.FOUR_SIZE;
			message.signature = new byte[signatureLength];
			System.arraycopy(msg, pos, message.signature, 0, signatureLength);
			pos += signatureLength;
		}
		return message;
	}
	
	public static void bytesConvertIntoEnvelope(byte[] udpPkt, MsgEnvelope msgEnv) throws IdentifierException {
		if (udpPkt == null || udpPkt.length < Common.MESSAGE_ENVELOPE_SIZE)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"无效的消息信封");
		msgEnv.protocolMajorVersion = udpPkt[0];
		msgEnv.protocolMinorVersion = udpPkt[1];
		msgEnv.compressed = (udpPkt[2] & Common.ENV_FLAG_COMPRESSED) != 0;
		msgEnv.encrypted = (udpPkt[2] & Common.ENV_FLAG_ENCRYPTED) != 0;
		msgEnv.truncated = (udpPkt[2] & Common.ENV_FLAG_TRUNCATED) != 0;
		msgEnv.suggestMajorProtocolVersion = (byte) (udpPkt[2] & 0x03);
		msgEnv.suggestMinorProtocolVersion = udpPkt[3];
		if (msgEnv.suggestMajorProtocolVersion == 0) {
			msgEnv.suggestMajorProtocolVersion = msgEnv.protocolMajorVersion;
			msgEnv.suggestMinorProtocolVersion = msgEnv.protocolMinorVersion;
		}
		msgEnv.sessionId = read4Bytes(udpPkt, 4);
		msgEnv.requestId = read4Bytes(udpPkt, 8);
		msgEnv.messageSequenceNum = read4Bytes(udpPkt, 12);
		msgEnv.messageLength = read4Bytes(udpPkt, 16);
		if (msgEnv.messageLength > Common.MAX_MESSAGE_LENGTH || msgEnv.messageLength < 0)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"Invalid message length: " + msgEnv.messageLength);
	}
	
	public static final int bytesConvertIntoIdentifierValue(byte[] buf, int offset, IdentifierValue value)
			throws IdentifierException {
		int origOffset = offset;
		value.index = read4Bytes(buf, offset);
		offset += Common.FOUR_SIZE;

		value.timestamp = read4Bytes(buf, offset); 
		offset += Common.FOUR_SIZE;

		value.ttlType = buf[offset++];

		value.ttl = read4Bytes(buf, offset); 
		offset += Common.FOUR_SIZE;

		byte permissions = buf[offset++]; 
		value.bAdminRead = (permissions & Common.PERM_ADMIN_READ) != 0;
		value.bAdminWrite = (permissions & Common.PERM_ADMIN_WRITE) != 0;
		value.bPublicRead = (permissions & Common.PERM_PUBLIC_READ) != 0;
		value.bPublicWrite = (permissions & Common.PERM_PUBLIC_WRITE) != 0;

		value.type = readByteArray(buf, offset); 
		offset += Common.FOUR_SIZE + value.type.length;

		value.data = readByteArray(buf, offset);
		offset += Common.FOUR_SIZE + value.data.length;

		value.references = new ValueReference[read4Bytes(buf, offset)];
		offset += Common.FOUR_SIZE; 
		for (int i = 0; i < value.references.length; i++) {
			value.references[i] = new ValueReference();
			value.references[i].identifier = readByteArray(buf, offset);
			offset += Common.FOUR_SIZE + value.references[i].identifier.length;
			value.references[i].index = read4Bytes(buf, offset);
			offset += Common.FOUR_SIZE;
		}
		return offset - origOffset;
	}
	
	public static final SiteInfo convertBodyBytestoSiteInfo(byte[] bodyData, int offset) throws IdentifierException{
		SiteInfo si = new SiteInfo();
		si.dataFormatVersion = bodyData[offset]<<8 | bodyData[offset + 1];//第2个byte是否需要&0x00ff
		offset += 2;
		
		si.majorProtocolVersion = bodyData[offset];
		offset += 1;
		si.minorProtocolVersion = bodyData[offset];
		offset += 1;
		
		si.serialNumber = bodyData[offset]<<8 | bodyData[offset + 1];
		offset += 2;
		
		si.isPrimarySite = (SiteInfo.PRIMARY_SITE & bodyData[offset]) > 0;
		si.isMultiPrimarySite = (SiteInfo.MULTI_PRIMARY & bodyData[offset]) > 0;
		offset += 1;
		
		si.hashOption = bodyData[offset];
		offset += 1;
		
		si.hashFilter = readByteArray(bodyData, offset);
		offset += Common.FOUR_SIZE + si.hashFilter.length;
		
		int arr_size = read4Bytes(bodyData, offset);
		offset += Common.FOUR_SIZE;
		if(arr_size <0 || arr_size > Common.MAX_ARRAY_SIZE)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR, "消息尺寸越界");
		
		si.attributes = new Attribute[arr_size];
		for(int i=0; i<si.attributes.length; i++) {
			si.attributes[i] = new Attribute();
			si.attributes[i].name = readByteArray(bodyData, offset);
			offset += Common.FOUR_SIZE + si.attributes[i].name.length;
			si.attributes[i].value = readByteArray(bodyData, offset);
			offset += Common.FOUR_SIZE + si.attributes[i].value.length;
		}
		
		int numberServ = read4Bytes(bodyData, offset);
		offset += Common.FOUR_SIZE;
		if(numberServ <0 || numberServ > Common.MAX_ARRAY_SIZE)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR, "消息尺寸越界");
		
		si.servers = new ServerInfo[numberServ];
		for(int j = 0; j < si.servers.length; j++) {
			si.servers[j] = new ServerInfo();
			si.servers[j].serverId = read4Bytes(bodyData, offset);
			offset += Common.FOUR_SIZE;
			
			si.servers[j].ipBytes = new byte[Common.IP_ADDRESS_SIZE_SIXTEEN];
			System.arraycopy(bodyData, offset, si.servers[j].ipBytes, 0, Common.IP_ADDRESS_SIZE_SIXTEEN);
			offset += Common.IP_ADDRESS_SIZE_SIXTEEN;
			
			si.servers[j].publicKey = readByteArray(bodyData, offset);
            offset += Common.FOUR_SIZE + si.servers[j].publicKey.length;
            
            int numberItems = read4Bytes(bodyData, offset);
            offset += Common.FOUR_SIZE;
            
            if(numberItems < 0 || numberItems > Common.MAX_ARRAY_SIZE)
    			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR, "消息尺寸越界");
            
            IdisCommunicationItems[] items = new IdisCommunicationItems[numberItems];
            si.servers[j].communicationItems = items;
            
            for(int k = 0; k < items.length; k++) {
            	items[k] = new IdisCommunicationItems();
            	items[k].type = bodyData[offset];
            	offset += 1;
            	items[k].protocol = bodyData[offset];
            	offset += 1;
            	items[k].port = read4Bytes(bodyData, offset);
            	offset += Common.FOUR_SIZE;
            }
            
		}
		if (offset < bodyData.length) 
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR, "解码后有冗余数据"); 
		return si;
	}
	////////////////////////////////////////////////////private-founctions//////////////////////////////////////////////////////////////////
	private static boolean hasDigestInMsgHeader(MsgHeader msgHeader){
		return msgHeader.responseCode != MessageCommon.RC_RESERVED && (Common.FLAG_9TH_MSG_RRDG & msgHeader.opFlags) != 0;
	}
	
	private static void populateMsgWithEnvelopAndOpFlags(BaseMessage message, MsgHeader msgHeader,
			MsgEnvelope envelope) {
		message.sessionId = envelope.sessionId;
		message.requestId = envelope.requestId;
		message.majorProtocolVersion = envelope.protocolMajorVersion;
		message.minorProtocolVersion = envelope.protocolMinorVersion;
		message.suggestMajorProtocolVersion = envelope.suggestMajorProtocolVersion;
		message.suggestMinorProtocolVersion = envelope.suggestMinorProtocolVersion;
		message.opCode = msgHeader.opCode;
		message.responseCode = msgHeader.responseCode;
		message.siteInfoSerial = msgHeader.serialNumber;
		message.recursionCount = msgHeader.recursionCount;

		int opFlag = msgHeader.opFlags;
		message.bAuthoritative = (Common.FLAG_1ST_MSG_AUTH & opFlag) != 0;
		message.bCertify = (Common.FLAG_2ND_MSG_CERT & opFlag) != 0;
		message.bEncrypt = (Common.FLAG_3RD_MSG_ENCR & opFlag) != 0;
		message.bRecursive = (Common.FLAG_4TH_MSG_RECU & opFlag) != 0;
		message.bCacheCertify = (Common.FLAG_5TH_MSG_CACR & opFlag) != 0;
		message.ignoreRestrictedValues = (Common.FLAG_8TH_MSG_PUBL & opFlag) != 0;
		message.continuous = (Common.FLAG_6TH_MSG_CONT & opFlag) != 0;
		message.bKeepAlive = (Common.FLAG_7TH_MSG_KPAL & opFlag) != 0;
		message.returnRequestDigest = (Common.FLAG_9TH_MSG_RRDG & opFlag) != 0;
		message.overwriteWhenExists = (Common.FLAG_10TH_MSG_OVRW & opFlag) != 0;
		message.mintNewSuffix = (Common.FLAG_11TH_MSG_MINT & opFlag) != 0;
		message.doNotRefer = (Common.FLAG_12TH_MSG_DNRF & opFlag) != 0;
		message.trustedQuery = (Common.FLAG_31ST_MSG_TRUSTED_QUERY & opFlag) != 0;
		if (message.trustedQuery) {
			message.trustedResult = ((Common.FLAG_32ND_MSG_TRUSTED_RESULT & opFlag) != 0)
					? Common.RESOLUTION_RESULT_TRUSTY : Common.RESOLUTION_RESULT_UNTRUSTY;
		} else {
			message.trustedResult = Common.RESOLUTION_RESULT_UNKNOWN;
		}
		message.expiration = msgHeader.exprTime;
	}
	
	private static ResolutionResponse convertBytesToResolutionResponse(byte[] msg, int offset, MsgEnvelope env)
			throws IdentifierException {
		int identifierLen = read4Bytes(msg, offset);
		offset += Common.FOUR_SIZE;

		if (identifierLen < 0 || identifierLen > Common.MAX_IDENTIFIER_LENGTH)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,"消息格式错误，无效标识长度: " + identifierLen);

		byte[] identifier = new byte[identifierLen];
		System.arraycopy(msg, offset, identifier, 0, identifierLen);
		offset += identifierLen;

		int numValues = read4Bytes(msg, offset);
		offset += Common.FOUR_SIZE;

		if (numValues < 0 || numValues > Common.MAX_IDENTIFIER_VALUES)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"消息格式错误，标识值个数异常:" + numValues);

		byte[][] values = new byte[numValues][];
		for (int i = 0; i < numValues; i++) {
			int valLen = calcIdentifierValueSize(msg, offset);
			values[i] = new byte[valLen];
			System.arraycopy(msg, offset, values[i], 0, valLen);
			offset += valLen;
		}
		return new ResolutionResponse(identifier, values);
	}
	
	private static BaseResponse convertBytesToCreateIdentifierResponse(byte[] msg, int offset, MsgEnvelope env, int bodyLength)
			throws IdentifierException {
		if (bodyLength == 0) {
			return new CreateIdentifierResponse(null);
		}

		int identifierLen = read4Bytes(msg, offset);
		offset += Common.FOUR_SIZE;

		if (identifierLen < 0 || identifierLen > Common.MAX_IDENTIFIER_LENGTH) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"无效的标识长度: " + identifierLen);
		}

		byte[] identifier = new byte[identifierLen];
		System.arraycopy(msg, offset, identifier, 0, identifierLen);
		offset += identifierLen;

		return new CreateIdentifierResponse(identifier);
	}
	
	private static BaseMessage convertBytesToServiceReferralResponse(int responseCode, byte[] msg, int offset,int endPos,
			MsgEnvelope env) throws IdentifierException {
		int identifierLen = read4Bytes(msg, offset);
		offset += Common.FOUR_SIZE;

		if (identifierLen < 0 || identifierLen > Common.MAX_IDENTIFIER_LENGTH)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					"Invalid identifier length: " + identifierLen);

		byte[] identifier = new byte[identifierLen];
		System.arraycopy(msg, offset, identifier, 0, identifierLen);
		offset += identifierLen;

		byte[][] values = null;
		if (offset < endPos) {
			int numValues = read4Bytes(msg, offset);
			offset += Common.FOUR_SIZE;

			if (numValues < 0 || numValues > Common.MAX_IDENTIFIER_VALUES)
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
						"无效的标识值个数: " + numValues);

			values = new byte[numValues][];
			for (int i = 0; i < numValues; i++) {
				int valLen = calcIdentifierValueSize(msg, offset);
				values[i] = new byte[valLen];
				System.arraycopy(msg, offset, values[i], 0, valLen);
				offset += valLen;
			}
		}
		return new ServiceReferralResponse(responseCode, identifier, values);
	}
	
	private static final BaseResponse convertBytesToErrorResponse(byte[] msg, int offset, int endPos, MsgEnvelope env) throws IdentifierException{
		return new ErrorResponse(readByteArray(msg, offset));
	}
	
	private static final ChallengeResponse convertBytesToChallengeResponse(byte[] msg, int offset, int opCode, MsgEnvelope env)
			throws IdentifierException {
		byte[] nonce = null;

		nonce = readByteArray(msg, offset);
		offset += Common.FOUR_SIZE + nonce.length;

		return new ChallengeResponse(opCode, nonce);
	}
	
	private static LoginIdisResponse convertBytesToLoginIdisResponse(byte[] msg, int offset, MsgEnvelope env){
		return new LoginIdisResponse(null, null);
	}
	
	private static GenericResponse convertBytesToGenericResponse(byte[] msg, int offset, MsgEnvelope env) {
		return new GenericResponse();
	}
	
	private static SiteResponse convertBytesToSiteResponse(byte[] msg, int offset, int length, MsgEnvelope env) throws IdentifierException{
		byte[] body = new byte[length];
		System.arraycopy(msg, offset, body, 0, length);
		SiteInfo site = convertBodyBytestoSiteInfo(body,0);
		return new SiteResponse(site);
	}
}
