package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class SiteResponse extends BaseResponse{
	
	private SiteInfo siteInfo;

	public SiteResponse(SiteInfo siteInfo){
		super(MessageCommon.OC_GET_SITE, MessageCommon.RC_SUCCESS);
		this.siteInfo = siteInfo;
	}
	
	public SiteInfo getSiteInfo() {
		return siteInfo;
	}

	public void setSiteInfo(SiteInfo siteInfo) {
		this.siteInfo = siteInfo;
	}
}
