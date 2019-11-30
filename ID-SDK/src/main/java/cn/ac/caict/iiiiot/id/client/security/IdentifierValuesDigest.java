package cn.ac.caict.iiiiot.id.client.security;
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
public class IdentifierValuesDigest {
	// 标识值对应的索引值
	public int index;
	// 标识值的摘要
	public String digest;
	
	public IdentifierValuesDigest(){}
	
	public IdentifierValuesDigest(int index, String digest){
		this.index = index;
		this.digest = digest;
	}
}
