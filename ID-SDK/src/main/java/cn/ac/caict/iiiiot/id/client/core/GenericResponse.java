package cn.ac.caict.iiiiot.id.client.core;
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
public class GenericResponse extends BaseResponse {

	public GenericResponse() {
		super();
	}

	public GenericResponse(int opCode, int responseCode) {
		super(opCode, responseCode);
	}

	public GenericResponse(BaseRequest req, int responseCode) throws IdentifierException {
		super(req, responseCode);
	}

}
