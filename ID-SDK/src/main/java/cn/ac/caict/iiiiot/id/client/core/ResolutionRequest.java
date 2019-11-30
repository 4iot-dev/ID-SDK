package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class ResolutionRequest extends BaseRequest {

	public byte[][] requestedTypes = null;
	public int[] requestedIndexes = null;
	public final boolean isAdminRequest = false;

	public ResolutionRequest(byte[] identifier, byte[][] reqTypes, int[] reqIndexes, AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_RESOLUTION, authInfo);
		this.requestedIndexes = reqIndexes;
		this.requestedTypes = reqTypes;
		this.authInfo = authInfo;
	}
	
	public ResolutionRequest(byte[] identifier, byte[][] reqTypes, int[] reqIndexes, AbstractAuthentication authInfo,
			boolean bTrusted) {
		this(identifier,reqTypes,reqIndexes,authInfo);
		this.trustedQuery = bTrusted;
	}

	private String getTypesString() {
		if (requestedTypes == null || requestedTypes.length <= 0)
			return "[ ]";
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i < requestedTypes.length; i++) {
			String type = Util.decodeString(requestedTypes[i]);
			sb.append(type);
			if(i != requestedTypes.length-1)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	private String getIndexesString() {
		if (requestedIndexes == null || requestedIndexes.length <= 0)
			return "[ ]";
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i < requestedIndexes.length; i++) {
			sb.append(requestedIndexes[i]);
			if(i != requestedIndexes.length-1)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	public String toString() {
		return super.toString() + ' ' + getTypesString() + ' ' + getIndexesString();
	}
}
