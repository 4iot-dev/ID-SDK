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
import cn.ac.caict.iiiiot.idisc.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;

public abstract class BaseMessage implements Cloneable {
	/************************************** 信封begin *******************************************/
	public int requestId = -1;// 一组请求与响应对应的一个标识
	public int sessionId = 0;// 多个连接共用一个sessionId（暂不使用）
	public byte majorProtocolVersion = MessageCommon.COMPATIBILITY_MAJOR_VERSION;
	public byte minorProtocolVersion = MessageCommon.COMPATIBILITY_MINOR_VERSION;
	public byte suggestMajorProtocolVersion = MessageCommon.MAJOR_VERSION;
	public byte suggestMinorProtocolVersion = MessageCommon.MINOR_VERSION;
	/************************************** 信封end *******************************************/
	/**
	 * 操作码，代表不同的操作
	 */
	public int opCode;
	/**
	 * 响应码，代表不同类型的响应
	 */
	public int responseCode = MessageCommon.RC_RESERVED;
	/**
	 * authoritative为权威位：request中设置此位为1，表示请求应该直接发到主服务站点（而不是镜像站点）;
	 * response设置此位为1，表示消息来自主服务器。
	 */
	public boolean bAuthoritative = false;
	/**
	 * bEncrypt为加密位，request时置为1，则需要服务器使用之前建立的会话密钥对其应答加密
	 */
	public boolean bEncrypt = false;
	/**
	 * bCertify为证明位，bCertify被置位的请求表示要求服务器对其应答进行签名。bCertify被置位的response表示消息已被签名。
	 * 如果请求时bCertify被置位，服务器必须对应答数据签名。如果服务器不能应答中提供有效的签名，idis-sdk应该丢弃应答并视此 请求失败。
	 */
	public boolean bCertify = false;
	/**
	 * bCacheCertify要求缓存服务器以客户端的身份对于所有的服务器应答进行验证（比如验证服务器签名）。
	 * response中bCacheCertify 置为true，表示应答数据已经缓存服务器验证通过。
	 */
	public boolean bCacheCertify = true;
	/**
	 * bKeepAlive为保持连接位，消息中设置为true，要求消息接收者保持TCP连接打开（在应答数据发回之后）。这使得同一TCP连接可用
	 * 于多个标识操作。
	 */
	public boolean bKeepAlive = false;
	/**
	 * returnRequestDigest为请求摘要位，request中置为true，要求服务器在其应答中包含消息摘要（message
	 * digest）。response消息中 returnRequestDigest置为true，表示消息体（Message
	 * Body）的第一个字段包含了原始请求的消息摘要。消息摘要可用于检查服务器 应答数据的完整性。
	 */
	public boolean returnRequestDigest = false;
	/**
	 * ignoreRestrictedValues,是否需要认证信息，为true没有任何限制（public），为false时需要认证信息
	 */
	public boolean ignoreRestrictedValues = true;
	/**
	 * bRecursive为递归请求位，如果为true,要求服务器以客户端的身份转发此请求到其他的idis服务上。
	 */
	public boolean bRecursive = true;
	/**
	 * 消息是否被截断。暂不用。
	 */
	public boolean continuous = false;
	/**
	 * 创建或者添加值时，如果为true即覆盖，如果为false不覆盖
	 */
	public boolean overwriteWhenExists = false;
	/**
	 * 发送创建标识请求时，让服务器生成一个新后缀
	 */
	public boolean mintNewSuffix = false;
	/**
	 * 请求服务器不发送引用响应
	 */
	public boolean doNotRefer = false;
	/**
	 * siteInfoSerial在请求中设置这个值是为了发送给服务端检查服务信息是否为最新。服务端在可能情况下尽可能满足客户端的请求，但响应
	 * 消息应该使用这个字段来指定最新的服务信息版本。如果服务端确实无法满足客户端请求，返回站点信息过期对应的返回码。
	 */
	public int siteInfoSerial = -1;
	/**
	 * expiration消息过期时间，单位秒(S)
	 */
	public int expiration;
	/**
	 * recursionCount递归次数，客户端发出的请求该字段值必须为0，该值是为了防止服务递归的无限循环
	 */
	public short recursionCount = 0;
	/**
	 * 是否进行可信解析查询,默认不进行可信解析查询，当该值为true时进行可信解析查询
	 */
	public boolean trustedQuery = false;
	/**
	 * 查询返回结果的可信情况：
	 * 		0代表返回的结果不确定是否可信
	 * 		1代表返回的结果可信
	 * 		2代表返回的结果不可信
	 */
	public int trustedResult = Common.RESOLUTION_RESULT_UNKNOWN; 

	public byte messageBody[] = null;
	
	public byte signature[] = null;
	
	public byte encodedMessage[] = null;
	/**
	 * 当这是一个响应消息时，它代表请求的摘要
	 */
	public byte requestDigest[] = null;
	/**
	 * 生成摘要使用的hash类型
	 */
	public byte rdHashType = Common.HASH_CODE_SHA1;
	/**
	 * 暂时无用的字段
	 */
	public byte signerIdf[] = null;
	
	public int signerIdfIdx = 0;
	
	public int sessionCounter = 0;

	public BaseMessage() {
		expiration = calculateExpiration();
	}

	public BaseMessage(int opCode) {
		this.opCode = opCode;
		expiration = calculateExpiration();
	}

	protected BaseMessage clone() {
		try {
			return (BaseMessage) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	private int calculateExpiration() {
		return (int) (System.currentTimeMillis() / 1000) + MessageCommon.MSG_EXPIRATION;
	}

	public void setSupportedProtocolVersion() {
		majorProtocolVersion = MessageCommon.MAJOR_VERSION;
		minorProtocolVersion = MessageCommon.MINOR_VERSION;
	}

	public void populateMsgSettings(BaseMessage msg) {
		this.suggestMajorProtocolVersion = msg.suggestMajorProtocolVersion;
		this.suggestMinorProtocolVersion = msg.suggestMinorProtocolVersion;
		this.returnRequestDigest = this.returnRequestDigest || msg.returnRequestDigest;
		this.majorProtocolVersion = msg.majorProtocolVersion;
		this.minorProtocolVersion = msg.minorProtocolVersion;
		this.recursionCount = msg.recursionCount;
		this.ignoreRestrictedValues = msg.ignoreRestrictedValues;
		this.doNotRefer = msg.doNotRefer;
		this.bAuthoritative = msg.bAuthoritative;
		this.bCacheCertify = msg.bCacheCertify;
		this.bEncrypt = msg.bEncrypt;
		this.bCertify = msg.bCertify;
	}

	public void clearBuffers() {
		encodedMessage = null;
		signature = null;
		messageBody = null;
	}

	public final byte[] getEncodedMessageBody() throws IdentifierException {
		if (messageBody != null)
			return messageBody;
		return (messageBody = MsgBytesConvertor.messageConvertIntoBytes(this));
	}

	public final byte[] getEncodedMessage() throws IdentifierException {
		// 如果已经生成消息，则返回消息...
		if (encodedMessage != null)
			return encodedMessage;
		// 生成消息体，如果还没有生成...
		getEncodedMessageBody();
		// 连接消息体和sig--签名（如果有）并返回结果
		encodedMessage = new byte[messageBody.length + Common.FOUR_SIZE + (signature == null ? 0 : signature.length)];
		System.arraycopy(messageBody, 0, encodedMessage, 0, messageBody.length);
		if (signature == null) {
			MsgBytesConvertor.write4Bytes(encodedMessage, messageBody.length, 0);
		} else {
			MsgBytesConvertor.write4Bytes(encodedMessage, messageBody.length, signature.length);
			System.arraycopy(signature, 0, encodedMessage, messageBody.length + Common.FOUR_SIZE, signature.length);
		}
		return encodedMessage;
	}

	public String toString() {
		return "version=" + ((int) majorProtocolVersion) + '.' + ((int) minorProtocolVersion) + "; oc=" + opCode
				+ "; rc=" + responseCode + "; snId=" + sessionId +
				(bCertify ? " crt" : "") + (bCacheCertify ? " caCrt" : "") + (bAuthoritative ? " auth" : "")
				+ (continuous ? " cont'd" : "") + (bEncrypt ? " encrypt" : "")
				+ (ignoreRestrictedValues ? " noAuth" : "")
				+ (expiration != 0 ? (" expires:" + new java.util.Date(expiration * 1000l)) : "");
	}

	public static Object getResponseCodeMessage(int responseCode) {
	    switch(responseCode) {
	      case MessageCommon.RC_RESERVED: return "RC_RESERVED";
	      case MessageCommon.RC_SUCCESS: return "SUCCESS";
	      case MessageCommon.RC_ERROR: return "ERROR";
	      case MessageCommon.RC_SERVER_TOO_BUSY: return "SERVER TOO BUSY";
	      case MessageCommon.RC_PROTOCOL_ERROR: return "PROTOCOL ERROR";
	      case MessageCommon.RC_IDENTIFIER_NOT_FOUND: return "IDENTIFIER NOT FOUND";
	      case MessageCommon.RC_IDENTIFIER_ALREADY_EXISTS: return "IDENTIFIER ALREADY EXISTS";
	      case MessageCommon.RC_INVALID_IDENTIFIER: return "INVALID IDENTIFIER";
	      case MessageCommon.RC_VALUES_NOT_FOUND: return "VALUES NOT FOUND";
	      case MessageCommon.RC_VALUE_ALREADY_EXISTS: return "VALUE ALREADY EXISTS";
	      case MessageCommon.RC_INVALID_VALUE: return "INVALID VALUE";
	      case MessageCommon.RC_SERVICE_REFERRAL: return "SERVICE REFERRAL";
	      case MessageCommon.RC_INVALID_ADMIN: return "INVALID ADMIN";
	      case MessageCommon.RC_INSUFFICIENT_PERMISSIONS: return "INSUFFICIENT PERMISSIONS";
	      case MessageCommon.RC_AUTHENTICATION_NEEDED: return "AUTHENTICATION NEEDED";
	      case MessageCommon.RC_AUTHENTICATION_FAILED: return "AUTHENTICATION FAILED";
	      case MessageCommon.RC_INVALID_CREDENTIAL: return "INVALID CREDENTIAL";
	      case MessageCommon.RC_AUTHEN_TIMEOUT: return "AUTHENTICATION TIMEOUT";
	      case MessageCommon.RC_PREFIX_REFERRAL: return "PREFIX REFERRAL";
	      case MessageCommon.RC_RELOGIN: return "CLIENT REPEAT LOGIN";
	      case MessageCommon.RC_LOGIN_FIRST: return "PLEASE LOGIN FIRST";
	      default:
	        return "UNKNOWN RESPONSE";
	    }
	  }
}
