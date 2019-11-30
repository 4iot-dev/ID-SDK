package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class CreateIdentifierResponse extends BaseResponse {

	public byte identifier[];
	
	public CreateIdentifierResponse(byte[] identifier) {
	    super(MessageCommon.OC_CREATE_IDENTIFIER, MessageCommon.RC_SUCCESS);
	    this.identifier = identifier;
	}
	
	public CreateIdentifierResponse(BaseRequest req, byte[] identifier) throws IdentifierException {
	    super(req, MessageCommon.RC_SUCCESS);
	    this.identifier = identifier;
	}
}
