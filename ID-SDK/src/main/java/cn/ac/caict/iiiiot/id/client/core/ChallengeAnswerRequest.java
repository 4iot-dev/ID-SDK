package cn.ac.caict.iiiiot.id.client.core;

import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

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
