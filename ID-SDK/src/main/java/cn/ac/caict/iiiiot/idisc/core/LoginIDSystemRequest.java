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

public class LoginIDSystemRequest extends BaseRequest {
	public int requestedIndexes = -1;
	public final boolean isAdminRequest = false;

	public LoginIDSystemRequest(byte[] identifier, int index,AbstractAuthentication authInfo) {
		super(identifier, MessageCommon.OC_LOGIN_ID_SYSTEM, authInfo);
		this.requestedIndexes = index;
		this.authInfo = authInfo;
	}

	public String toString() {
		return super.toString() + " index:" + requestedIndexes;
	}
}
