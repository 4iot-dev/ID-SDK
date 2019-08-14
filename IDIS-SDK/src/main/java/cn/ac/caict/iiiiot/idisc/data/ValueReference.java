package cn.ac.caict.iiiiot.idisc.data;
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
import java.util.Arrays;

import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class ValueReference {
	private final static int COLON_CHAR = ':';
	private final static int ZERO_CHAR = '0';
	private final static int NINE_CHAR = '9';
	public byte[] identifier;
	public int index;
	
	public ValueReference() {
	}

	public ValueReference(byte[] identifier, int index) {
		this.identifier = identifier;
		this.index = index;
	}

	public ValueReference(String identifierString, int index) {
		this.identifier = Util.encodeString(identifierString);
		this.index = index;
	}

	/**
	 * 说明：将字符串转换为ValueReference对象，如"300:88.1000.1/sample"
	 * @param str 待转换字符串
	 * @return ValueReference对象
	 */
	public static ValueReference transStr2ValueReference(String str) {
		if (str == null)
			return null;
		int colon_index = str.indexOf(COLON_CHAR);
		if (colon_index < 0)
			return new ValueReference(Util.encodeString(str), 0);
		String maybeIndex = str.substring(0, colon_index);
		if (isDigits(maybeIndex)) {
			String identifier = str.substring(colon_index + 1);
			return new ValueReference(Util.encodeString(identifier), Integer.parseInt(maybeIndex));
		}
		return new ValueReference(Util.encodeString(str), 0);
	}

	public String getIdentifierAsString() {
		return Util.decodeString(identifier);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(Util.upperCasePrefix(identifier));
		result = Common.SUITABLE_PRIME_NUMBER * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueReference other = (ValueReference) obj;
		if (!Util.equalsPrefixCaseInsensitive(identifier, other.identifier))
			return false;
		if (index != other.index)
			return false;
		return true;
	}

	public boolean isMatchedBy(ValueReference other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!Util.equalsPrefixCaseInsensitive(identifier, other.identifier))
			return false;
		if (index != 0 && index != other.index)
			return false;
		return true;
	}
	
	public String toString() {
		return String.valueOf(index) + COLON_CHAR + Util.decodeString(identifier);
	}
	
	private static boolean isDigits(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch < ZERO_CHAR || ch > NINE_CHAR)
				return false;
		}
		return true;
	}
}
