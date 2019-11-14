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
import java.io.InputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Signature;
import java.util.logging.Logger;

import cn.ac.caict.iiiiot.idisc.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public abstract class BaseResponse extends BaseMessage {
	public InputStream stream = null;
	public Socket socket = null;
	public boolean secureStream = false;

	public BaseResponse() {
		super();
	}

	public BaseResponse(int opCode, int responseCode) {
		super(opCode);
		this.responseCode = responseCode;
	}

	public BaseResponse(BaseRequest req, int responseCode) throws IdentifierException {
		super(req.opCode);
		this.responseCode = responseCode;
		init(req);
	}

	public void init(BaseRequest req) throws IdentifierException {
		populateMsgSettings(req);
		this.suggestMajorProtocolVersion = MessageCommon.MAJOR_VERSION;
		this.suggestMinorProtocolVersion = MessageCommon.MINOR_VERSION;
		this.majorProtocolVersion = MessageCommon.MAJOR_VERSION;
		this.minorProtocolVersion = MessageCommon.MINOR_VERSION;
		this.sessionId = req.sessionId;
		this.requestId = req.requestId;
		if (this.returnRequestDigest) {
			extractRequestDigest(req);
		}
	}

	public BaseResponse getContinuedResponse() {
		return null;
	}

	public final void extractRequestDigest(BaseMessage req) throws IdentifierException {
		requestDigest = Util.doSHA256Digest(req.getEncodedMessageBody());
		rdHashType = Common.HASH_CODE_SHA256;
	}

	public final boolean checkCredential(PublicKey pubKey) throws Exception {
		if (signature == null || signature.length <= 0) {
			return false;
		}
		int offset = 0;
		byte version = signature[offset++];
		byte reserved = signature[offset++];
		int Options = BaseConvertor.read2Bytes(signature, offset);
		offset += Common.TWO_SIZE;

		byte[] tmp_idle = BaseConvertor.readByteArray(signature, offset);
		offset += Common.FOUR_SIZE + tmp_idle.length;
		int other = BaseConvertor.read4Bytes(signature, offset);
		offset += Common.FOUR_SIZE;

		byte[] signType =  BaseConvertor.readByteArray(signature, offset);
		offset += Common.FOUR_SIZE + signType.length;

		if (!Util.equalsBytes(signType, Common.CREDENTIAL_TYPE_SIGNED)) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_UNKNOWN_ALGORITHM_ID,
					"未知的签名类型：" + Util.decodeString(signType));
		}

		int sigLen = BaseConvertor.read4Bytes(signature, offset);
		offset += Common.FOUR_SIZE;

		byte hashAlgBytes[] = BaseConvertor.readByteArray(signature, offset);
		offset += Common.FOUR_SIZE + hashAlgBytes.length;

		byte sigBytes[] = BaseConvertor.readByteArray(signature, offset);
		String algorithm = Util.getSigAlgFromSignKeyType(hashAlgBytes, pubKey.getAlgorithm());
		Signature sig = Signature.getInstance(algorithm);

		sig.initVerify(pubKey);
        byte[] toBeSigned = new byte[4 + 3 * Common.FOUR_SIZE]; 
        offset = 0;
        toBeSigned[offset++] = majorProtocolVersion;
        toBeSigned[offset++] = minorProtocolVersion;
        toBeSigned[offset++] = suggestMajorProtocolVersion;
        toBeSigned[offset++] = suggestMinorProtocolVersion;
        offset += BaseConvertor.write4Bytes(toBeSigned, offset, sessionId);
        offset += BaseConvertor.write4Bytes(toBeSigned, offset, requestId);
        offset += BaseConvertor.write4Bytes(toBeSigned, offset, sessionCounter);
        sig.update(toBeSigned);
		sig.update(getEncodedMessageBody());
		return sig.verify(sigBytes);
	}
}
