package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class DeleteIdentifierRequest extends BaseRequest {
	public DeleteIdentifierRequest(byte[] identifier, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_DELETE_IDENTIFIER, authInfo);
	}
}
