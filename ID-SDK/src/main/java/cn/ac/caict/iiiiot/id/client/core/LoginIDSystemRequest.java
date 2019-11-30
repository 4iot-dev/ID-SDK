package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class LoginIDSystemRequest extends BaseRequest {
	public int requestedIndexes = -1;
	public final boolean isAdminRequest = false;

	public LoginIDSystemRequest(byte[] identifier, int index,AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_LOGIN_ID_SYSTEM, authInfo);
		this.requestedIndexes = index;
		this.authInfo = authInfo;
	}

	public String toString() {
		return super.toString() + " index:" + requestedIndexes;
	}
}
