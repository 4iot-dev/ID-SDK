package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class AddValueRequest extends BaseRequest {

	public IdentifierValue[] values;

	public AddValueRequest(byte[] identifier, IdentifierValue value, AbstractAuthentication authInfo) {
		this(identifier, new IdentifierValue[] { value }, authInfo);
	}

	public AddValueRequest(byte[] identifier, IdentifierValue[] values, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_ADD_VALUE, authInfo);
		this.values = values;
	}
}
