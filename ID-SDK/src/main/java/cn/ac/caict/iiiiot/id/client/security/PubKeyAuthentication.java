package cn.ac.caict.iiiiot.id.client.security;
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
import java.security.*;

import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.id.client.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.id.client.core.BaseRequest;
import cn.ac.caict.iiiiot.id.client.core.ChallengeResponse;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class PubKeyAuthentication extends AbstractAuthentication {
	private Log logger = IDLog.getLogger(PubKeyAuthentication.class);
	private PrivateKey privateKey;
	private byte[] userIdIdentifier;
	private int userIdIndex;

	public PubKeyAuthentication(byte[] userIdIdentifier, int userIdIndex, PrivateKey privateKey) {
		this.privateKey = privateKey;
		this.userIdIdentifier = userIdIdentifier;
		this.userIdIndex = userIdIndex;
	}

	public byte[] getTypeAuth() {
		return Common.TYPE_PUBLIC_KEY;
	}

	public byte[] getUserIdentifier() {
		return userIdIdentifier;
	}

	public int getUserIndex() {
		return userIdIndex;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public byte[] authenticateAction(ChallengeResponse challenge, BaseRequest request) throws IdentifierException {
		boolean bChanged = isDigistChanged(challenge,request);
		logger.info("bChange is " + bChanged);
		if (bChanged) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, "安全警告，摘要已发生变化，无法签名!");
		}
		return doSignature(challenge);
	}

	public String toString() {
		return String.valueOf(userIdIndex) + ":" + ((userIdIdentifier == null) ? "null" : Util.decodeString(userIdIdentifier));
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof PubKeyAuthentication))
			return false;
		if (super.equals(obj))
			return true;
		PubKeyAuthentication other = (PubKeyAuthentication) obj;
		boolean sameIdentity = Util.equalsBytes(getUserIdentifier(), other.getUserIdentifier())
				&& getUserIndex() == other.getUserIndex();
		if (!sameIdentity)
			return false;
		byte[] encKey = privateKey.getEncoded();
		byte[] otherEncKey = other.privateKey.getEncoded();
		if (encKey == null || otherEncKey == null)
			return false;
		return Util.equalsBytes(encKey, otherEncKey);
	}
	
	private boolean isDigistChanged(ChallengeResponse challenge, BaseRequest request) throws IdentifierException{
		byte origDigest[] = Util.doDigest(challenge.rdHashType, request.getEncodedMessageBody());
		return !Util.equalsBytes(origDigest, challenge.requestDigest);
	}
	
	private byte[] doSignature(ChallengeResponse challenge) throws IdentifierException{
		logger.info("doSignature--method--begin");
		System.err.println(Util.bytesToHexString(challenge.nonce));
		System.err.println(Util.bytesToHexString(challenge.requestDigest));
		byte signatureBytes[] = null;
		byte sigHashType[] = null;
		try {
			Signature signer = null;
			String alg = privateKey.getAlgorithm().trim();
			signer = Signature.getInstance(Util.getDefaultSigId(alg, challenge));
			signer.initSign(privateKey);
			signer.update(challenge.nonce);
			signer.update(challenge.requestDigest);
			signatureBytes = signer.sign();
			logger.info("signer algorithm :" + signer.getAlgorithm());
			sigHashType = Util.getHashAlgIdFromSigId(signer.getAlgorithm());
			logger.info("signatureBytes.length=" + signatureBytes.length);
			int offset = 0;
			byte signature[] = new byte[signatureBytes.length + sigHashType.length + 2 * Common.FOUR_SIZE];
			offset += BaseConvertor.writeByteArray(signature, offset, sigHashType);
			offset += BaseConvertor.writeByteArray(signature, offset, signatureBytes);
			logger.info("doSignature--method--end");
			return signature;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			logger.error("无法为该质询操作签名:" + e);
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR, "无法为该质询操作签名: ", e);
		}
	}
}
