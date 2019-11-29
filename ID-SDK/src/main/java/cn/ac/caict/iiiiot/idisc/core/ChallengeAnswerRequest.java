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
import cn.ac.caict.iiiiot.idisc.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class ChallengeAnswerRequest extends BaseRequest {
	public byte[] authType;
	public byte[] userIdIdentifier;
	public int userIdIndex;
	public byte[] signedResponse;
	public BaseRequest originalRequest;

	public ChallengeAnswerRequest(byte[] authType, byte[] userIdIdentifier, int userIdIndex, byte[] signedResponse,
			AbstractAuthentication authInfo) {
		super(Common.BLANK_IDENTIFIER, MessageCommon.OC_RESPONSE_TO_CHALLENGE, authInfo);
		this.authType = authType;
		this.userIdIdentifier = userIdIdentifier;
		this.userIdIndex = userIdIndex;
		this.signedResponse = signedResponse;
	}

	public ChallengeAnswerRequest(BaseRequest req, ChallengeResponse challenge, AbstractAuthentication authInfo)
			throws IdentifierException {
		this(authInfo.getTypeAuth(), authInfo.getUserIdentifier(), authInfo.getUserIndex(),
				authInfo.authenticateAction(challenge, req), authInfo);
		populateMsgSettings(req);
		sessionId = challenge.sessionId;
	}

	public String toString() {
		return super.toString() + ' ' + Util.decodeString(authType) + ' ' + userIdIndex + ':'
				+ Util.decodeString(userIdIdentifier);
	}
}
