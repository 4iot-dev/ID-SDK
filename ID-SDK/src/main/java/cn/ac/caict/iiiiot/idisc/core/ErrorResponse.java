package cn.ac.caict.iiiiot.idisc.core;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *© COPYRIGHT 2019 Corporation for Institute of Industrial Internet & Internet of Things (IIIIT);
 *                      All rights reserved. 
 * http://www.caict.ac.cn  
 * https://www.citln.cn/
 */
import cn.ac.caict.iiiiot.idisc.utils.Util;

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
