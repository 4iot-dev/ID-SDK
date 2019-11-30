package cn.ac.caict.iiiiot.id.client.core;

import cn.ac.caict.iiiiot.id.client.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class ServiceReferralResponse extends BaseResponse {

	public byte[] identifier;
	public byte[][] values;

	public ServiceReferralResponse(int responseCode, byte[] identifier, byte[][] values) {
		super(MessageCommon.OC_RESOLUTION, responseCode);
		this.identifier = identifier;
		this.values = values;
	}

	public ServiceReferralResponse(BaseRequest req, int responseCode, byte[] identifier, byte[][] clumps)
			throws IdentifierException {
		super(req, responseCode);
		this.identifier = identifier;
		this.values = clumps;
	}

	public IdentifierValue[] getIdentifierValue() throws IdentifierException {
		if (values == null)
			return null;
		IdentifierValue retValues[] = new IdentifierValue[values.length];
		for (int i = 0; i < retValues.length; i++) {
			retValues[i] = new IdentifierValue();
			BytesMsgConvertor.bytesConvertIntoIdentifierValue(values[i], 0, retValues[i]);
		}
		return retValues;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(' ');
		if (identifier == null)
			sb.append(String.valueOf(identifier));
		else
			sb.append(Util.decodeString(identifier));
		sb.append("\n");

		if (values != null) {
			try {
				IdentifierValue vals[] = getIdentifierValue();
				for (int i = 0; i < vals.length; i++) {
					sb.append("   ");
					sb.append(String.valueOf(vals[i]));
					sb.append('\n');
				}
			} catch (IdentifierException e) {
			}
		}
		return sb.toString();
	}
}
