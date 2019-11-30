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
import java.security.*;
import java.util.Random;

import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class ChallengeResponse extends BaseResponse {
	private static Random random = null;
	private static final String SYNLOCK = "synLock";
	public byte[] nonce;

	public ChallengeResponse(int opCode, byte nonce[]) {
		super(opCode, MessageCommon.RC_AUTHENTICATION_NEEDED);
		this.nonce = nonce;
	}

	public ChallengeResponse(BaseRequest req) throws IdentifierException {
		super(req, MessageCommon.RC_AUTHENTICATION_NEEDED);
		if (requestDigest == null) 
			extractRequestDigest(req);
		this.returnRequestDigest = true;
		this.nonce = generateNonce();
	}

	public static final void initializeRandom() {
		if (random == null) {
			synchronized (SYNLOCK) {
				if (random == null) {
					random = new SecureRandom();
					random.setSeed(System.nanoTime()); 
					random.nextInt();
				}
			}
		}
	}

	public static byte[] generateNonce() {
		byte[] nonce = new byte[Common.CHALLENGE_NONCE_SIZE];
		getRandom().nextBytes(nonce);
		return nonce;
	}

	private static Random getRandom() {
		if (random == null)
			initializeRandom();
		return random;
	}
}
