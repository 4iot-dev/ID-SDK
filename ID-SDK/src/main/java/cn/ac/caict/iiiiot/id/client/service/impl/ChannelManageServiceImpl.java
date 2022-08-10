package cn.ac.caict.iiiiot.id.client.service.impl;
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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.IdentifierResolveEngine;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;

public class ChannelManageServiceImpl implements IChannelManageService {
	private static final int CLOSED = 0;
	private static final int LOGIN = 1;
	private static final int LOGOUT = 2;
	private AtomicInteger channelCount = new AtomicInteger(0);
	private Log log = IDLog.getLogger(IDManageServiceChannelImpl.class);
	private static final String[] BANNER = {
			"#### ########           ######  ########  ##    ##",
			" ##  ##     ##         ##    ## ##     ## ##   ##",
			" ##  ##     ##         ##       ##     ## ##  ##",
			" ##  ##     ## #######  ######  ##     ## #####",
			" ##  ##     ##               ## ##     ## ##  ##",
			" ##  ##     ##         ##    ## ##     ## ##   ##",
			"#### ########           ######  ########  ##    ##"
	};
	
	static {
		for (String string : BANNER) {
			System.out.println(string);
		}
	}
	
	public void setChannelCount(int channelCount) {
		this.channelCount.set(channelCount);
	}
	
	@Override
	public int getIDManageServiceChannelCount() {
		return channelCount.get();
	}
	
	@Override
	public synchronized IIDManageServiceChannel generateChannel(String ip, int port, String protocol) throws IdentifierException {
		IdentifierResolveEngine resolverEngine = new IdentifierResolveEngine(ip, port, protocol);
		IIDManageServiceChannel channelService = new IDManageServiceChannelImpl(resolverEngine);
		log.debug("创建前通道连接数：" + channelCount.get());
		int after = channelCount.incrementAndGet();
		log.debug("创建后通道连接数：" + after);
		return channelService;
	}
	
	@Override
	public synchronized IIDManageServiceChannel generateChannelByConfig() throws IdentifierException, IOException {
		IdentifierResolveEngine resolverEngine = new IdentifierResolveEngine();
		IIDManageServiceChannel channelService = new IDManageServiceChannelImpl(resolverEngine);
		log.debug("创建前通道连接数：" + channelCount.get());
		int after = channelCount.incrementAndGet();
		log.debug("创建后通道连接数：" + after);
		return channelService;
	}

	@Override
	public synchronized void closeChannel(IIDManageServiceChannel channel) throws IdentifierException {
		if (channel == null){
			log.warn("当前连接通道不存在！");
			return;
		}
		if(channel instanceof IDManageServiceChannelImpl){
			((IDManageServiceChannelImpl) channel).logout();
		}
		log.debug("关闭前通道连接数：" + channelCount.get());
		int after = channelCount.decrementAndGet();
		log.debug("关闭后通道连接数：" + after);
	}

	@Override
	public int getIDManageServiceChannelState(IIDManageServiceChannel channel) {
		IdentifierResolveEngine resolverEngine = null;
		if(channel instanceof IDManageServiceChannelImpl){
			resolverEngine = ((IDManageServiceChannelImpl) channel).getResolverEngine();
		}
		if( channel == null || resolverEngine == null)
			return CLOSED;
		if(channel.isLogin()){
			return LOGIN;
		} else {
			return LOGOUT;
		}
	}

	@Override
	public String getChannelUserIdentifier(IIDManageServiceChannel channel) {
		String userIdentifier = null;
		if(channel instanceof IDManageServiceChannelImpl)
			userIdentifier = ((IDManageServiceChannelImpl) channel).getUserIdentifier();
		return userIdentifier;
	}
}
