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
import cn.ac.caict.iiiiot.idisc.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.idisc.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class LoginIdisResponse extends BaseResponse {

	public byte[] identifier;
	public byte[][] values;

	public LoginIdisResponse(byte[] identifier, byte[][] values) {
		super(MessageCommon.OC_LOGIN_IDIS, MessageCommon.RC_SUCCESS);
		this.identifier = identifier;
		this.values = values;
	}

	public IdentifierValue[] getIdfValues() throws IdentifierException {
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
				IdentifierValue vals[] = getIdfValues();
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
