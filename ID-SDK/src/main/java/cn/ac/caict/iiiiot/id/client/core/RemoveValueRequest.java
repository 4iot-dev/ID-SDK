package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class RemoveValueRequest extends BaseRequest {

	public int[] indexes;

	public RemoveValueRequest(byte[] identifier, int index, AbstractAuthentication authInfo) {
		this(identifier, new int[] { index }, authInfo);
	}

	public RemoveValueRequest(byte[] identifier, int[] indexes, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_REMOVE_VALUE, authInfo);
		this.indexes = indexes;
		this.isAdminRequest = false;
	}
}
