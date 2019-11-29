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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ac.caict.iiiiot.idisc.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.idisc.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class ResolutionResponse extends BaseResponse {
	public byte[] identifier;
	public byte[][] values;

	public ResolutionResponse(byte[] identifier, byte[][] values) {
		super(MessageCommon.OC_RESOLUTION, MessageCommon.RC_SUCCESS);
		this.identifier = identifier;
		this.values = values;
	}

	public ResolutionResponse(BaseRequest req, byte[] identifier, byte[][] clumps) throws IdentifierException {
		super(req, MessageCommon.RC_SUCCESS);
		this.identifier = identifier;
		this.values = clumps;
	}

	public ResolutionResponse(BaseRequest req, byte[] identifier, IdentifierValue[] IdentifierValues)
			throws IdentifierException {
		super(req, MessageCommon.RC_SUCCESS);
		this.identifier = identifier;
		this.values = new byte[IdentifierValues.length][];
		for (int i = 0; i < IdentifierValues.length; i++) {
			values[i] = MsgBytesConvertor.convertIdentifierValueToByte(IdentifierValues[i]);
		}
	}

	public IdentifierValue[] getAllIDValues() throws IdentifierException {
		if(values == null)
			return null;
		IdentifierValue retValues[] = new IdentifierValue[values.length];
		for (int i = 0; i < retValues.length; i++) {
			retValues[i] = new IdentifierValue();
			BytesMsgConvertor.bytesConvertIntoIdentifierValue(values[i], 0, retValues[i]);
		}
		return retValues;
	}
	
	public List<IdentifierValue> getAllIDValuesByIndexOrder() throws IdentifierException {
		if(values == null)
			return null;
		IdentifierValue vals[] = getAllIDValues();
		List<IdentifierValue> orderValues = Arrays.asList(vals);
		Collections.sort(orderValues, new Comparator<IdentifierValue>() {
			@Override
			public int compare(IdentifierValue var1, IdentifierValue var2) {
				return var1.getIndex()-var2.getIndex();
			}
		});
		return orderValues;
	}

	public IdentifierValue getValuesByIndex(int index) throws IdentifierException {
		IdentifierValue[] allValues = getAllIDValues();
		if (allValues == null)
			return null;
		for(int i=0; i<allValues.length; i++){
			if(allValues[i].getIndex() == index)
				return allValues[i];
		}
		return null;
	}
	
	public List<IdentifierValue> getValuesByType(String type) throws IdentifierException {
		List<IdentifierValue> allValues = getAllIDValuesByIndexOrder();
		if (allValues == null)
			return null;
		List<IdentifierValue> retValues = new ArrayList<IdentifierValue>();
		for(int i=0; i<allValues.size(); i++){
			if(allValues.get(i).getTypeStr().equalsIgnoreCase(type))
				retValues.add(allValues.get(i));
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
				IdentifierValue vals[] = getAllIDValues();
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
