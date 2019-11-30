package cn.ac.caict.iiiiot.id.client.service;
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
import java.io.IOException;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;

public interface IChannelManageService {
	/**
	 * @param ip 连接的ip地址
	 * @param port 连接的端口号
	 * @param protocol 数据传输采用协议类型，TCP还是UDP
	 * @return IDFManageServiceChannel 返回连接通道对象
	 * @throws IdentifierException
	 */
	public IIDManageServiceChannel generateChannel(String ip,int port, String protocol) throws IdentifierException;
	/**
	 * 调用者在所在工程项目的src/目录下构建config.json配置信息，信息格式如下：
	 * {
   	 *		"ip":"127.0.0.1",
   	 *		"port":"1999",
   	 *		"query":true,
   	 *		"admin":true,
   	 *		"protocol":"TCP"
	 *	}
	 * @return IDFManageServiceChannel 返回连接通道对象
	 * @throws IdentifierException
	 * @throws IOException
	 */
	public IIDManageServiceChannel generateChannelByConfig() throws IdentifierException, IOException;
	/**
	 * @param channel 目标通道
	 * @throws IdentifierException
	 */
	public void closeChannel(IIDManageServiceChannel channel) throws IdentifierException;
	/**
	 * @param channel 目标通道
	 * @return 返回目标通道的状态，0代表连接通道已关闭，1代表连接通道已登录，2代表连接通道未登录
	 */
	public int getIDManageServiceChannelState(IIDManageServiceChannel channel);
	/**
	 * @return 返回建立通道个数
	 */
	public int getIDManageServiceChannelCount();
	/**
	 * 获取该通道登录用户的标识名称
	 * @return 登录用户标识名称，未登录返回null
	 */
	public String getChannelUserIdentifier(IIDManageServiceChannel channel);
}
