package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class LoginRequest extends BaseRequest {

	public byte[][] requestedTypes = null;
	public int[] requestedIndexes= null;
	public final boolean isAdminRequest = false;

	public LoginRequest(byte[] identifier, byte[][] reqTypes, int[] reqIndexes, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_LOGIN, authInfo);
		this.requestedIndexes = reqIndexes;
		this.requestedTypes = reqTypes;
		this.authInfo = authInfo;
	}
}
