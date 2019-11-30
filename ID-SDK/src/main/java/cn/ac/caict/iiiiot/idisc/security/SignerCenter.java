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
import java.security.PrivateKey;

import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.log.IDLog;
import cn.ac.caict.iiiiot.idisc.utils.JsonWorker;

public class SignerCenter {
	
	private static SignerCenter signer = new SignerCenter();
	
	private Log logger = IDLog.getLogger(SignerCenter.class);
    
    public static SignerCenter getInstance() {
        return signer;
    }
    
    public SignatureStruct signClaims(Claims claims, PrivateKey privateKey) throws IdentifierException {
    	String payload = JsonWorker.getGson().toJson(claims);
    	logger.info("payload:" + payload);
    	SignatureStruct signature = new SignatureStructImpl(payload, privateKey);
        return signature;
    }

}
