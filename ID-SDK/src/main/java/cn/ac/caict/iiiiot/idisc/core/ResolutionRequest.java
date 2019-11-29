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
import cn.ac.caict.iiiiot.idisc.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

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
