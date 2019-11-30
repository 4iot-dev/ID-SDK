package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class CreateIdentifierRequest extends BaseRequest {
	public IdentifierValue[] values;

	public CreateIdentifierRequest(byte[] identifier, IdentifierValue[] values, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_CREATE_IDENTIFIER, authInfo);
		this.values = values;
	}

	public CreateIdentifierRequest(byte[] prefix, IdentifierValue[] values, AbstractAuthentication authInfo, boolean mintNewSuffix) {
		super(prefix, MessageCommon.OC_CREATE_IDENTIFIER, authInfo);
		this.values = values;
		this.isAdminRequest = true;
		this.mintNewSuffix = mintNewSuffix;
	}
}
