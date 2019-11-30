package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class ModifyValueRequest extends BaseRequest {

	public IdentifierValue[] values;

	public ModifyValueRequest(byte[] identifier, IdentifierValue value, AbstractAuthentication authInfo) {
		this(identifier, new IdentifierValue[] { value }, authInfo);
	}

	public ModifyValueRequest(byte[] identifier, IdentifierValue[] values, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_MODIFY_VALUE, authInfo);
		this.values = values;
		this.isAdminRequest = false;
	}
}
