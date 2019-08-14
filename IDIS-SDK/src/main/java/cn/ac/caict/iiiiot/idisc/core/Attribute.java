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
import java.util.Arrays;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class Attribute {
	public byte[] name;
	public byte[] value;

	public Attribute() {}

	public Attribute(byte[] name, byte[] value) {
		this.name = name;
		this.value = value;
	}

	public String toString() {
		if (name != null && value != null)
			return Util.decodeString(name) + ':' + Util.decodeString(value);
		if (name != null)
			return Util.decodeString(name) + ':';
		if (value != null)
			return ":" + Util.decodeString(value);
		return "[ ]";

	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(name);
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(value);
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
		Attribute other = (Attribute) obj;
		if (!Arrays.equals(name, other.name))
			return false;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}
}
