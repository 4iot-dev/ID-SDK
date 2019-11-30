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
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public abstract class BaseRequest extends BaseMessage {
	public byte[] identifier;
	public boolean isAdminRequest = false;
	public boolean requiresConnection = false;

	public boolean multithread = false;
	public ReentrantLock connectionLock = new ReentrantLock();
	public AtomicBoolean completed = new AtomicBoolean();
	public AtomicReference<Socket> socketRef = new AtomicReference<Socket>();

	public AbstractAuthentication authInfo = null;
	public int siteInfoSerial = -1;
	byte[] serverPubKeyBytes = null;

	public BaseRequest(byte[] identifier, int opCode, AbstractAuthentication authInfo) {
		super(opCode);
		this.authInfo = authInfo;
		this.identifier = identifier;
		this.responseCode = MessageCommon.RC_RESERVED;
	}

	public BaseRequest clone() {
		BaseRequest req = (BaseRequest) super.clone();
		req.socketRef = new AtomicReference<Socket>(this.socketRef.get());
		return req;
	}

	public void clearBuffers() {
		this.multithread = false;
		this.completed.set(false);
		this.socketRef.set(null);
		super.clearBuffers();
	}

	public String toString() {
		return super.toString() + (isAdminRequest ? " admin" : "") + ' ' + Util.decodeString(identifier);
	}
}
