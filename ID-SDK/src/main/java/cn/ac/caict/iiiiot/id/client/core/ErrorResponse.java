package cn.ac.caict.iiiiot.id.client.core;

import cn.ac.caict.iiiiot.id.client.utils.Util;

public class ErrorResponse extends BaseResponse {

	public byte[] message;

	public ErrorResponse(byte[] message) {
		this.message = message;
	}

	public ErrorResponse(int opCode, int responseCode, byte[] message) {
		super(opCode, responseCode);
		this.message = message;
	}

	public ErrorResponse(BaseRequest req, int errorCode, byte[] message) throws IdentifierException {
		super(req, errorCode);
		this.message = message;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (message != null && message.length > 0) {
			String msg = Util.decodeString(message);
			sb.append("ErrorCode: ").append(responseCode).append(", ")
					.append(BaseMessage.getResponseCodeMessage(responseCode)).append(": ").append(msg);
		} else {
			sb.append("ErrorCode: ").append(responseCode).append(", ")
					.append(BaseMessage.getResponseCodeMessage(responseCode));
		}
		return sb.toString();
	}
}
