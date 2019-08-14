package cn.ac.caict.iiiiot.idisc.security;
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
import cn.ac.caict.iiiiot.idisc.core.BaseRequest;
import cn.ac.caict.iiiiot.idisc.core.ChallengeResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.data.ValueReference;

public abstract class AbstractAuthentication {
	/**
	 * 获取标识由此认证对象表示的用户的标识名称。
	 * @return 标识名称字节数组
	 */
	public abstract byte[] getUserIdentifier();

	/**
	 * 获取标识该用户的标识值的索引。 识别此用户的标识的返回索引值应包含与该用户验证方式相对应的类型（公钥，密钥等）的值。
	 * @return 用户索引
	 */
	public abstract int getUserIndex();
	/**
	 * 获取认证类型，公私钥认证 or 密钥认证
	 * @return 认证类型
	 */
	public abstract byte[] getTypeAuth();

	/**
	 * 将给定的随机数和requestDigest标记为给定请求的质询。 该方法的实现也应该可以验证客户端实际上是发送指定的请求，
	 * 并且相关联的摘要是请求的有效摘要。 返回--随机数和requestDigest的并置签名。
	 * @param challenge 发起质询
	 * @param request 原始请求
	 * @return 签名数据
	 */
	public abstract byte[] authenticateAction(ChallengeResponse challenge, BaseRequest request) throws IdentifierException;

	/**
	 * 获取认证对象的引用数据
	 * @return 返回由Index、identifier组成的ValueReference对象
	 */
	public ValueReference getUserValueReference() {
		return new ValueReference(getUserIdentifier(), getUserIndex());
	}
}
