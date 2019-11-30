package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class SiteRequest extends BaseRequest {
	public SiteRequest() {
		super(Common.BLANK_IDENTIFIER, MessageCommon.OC_GET_SITE, null);
	}
}
