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
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.ac.caict.iiiiot.id.client.utils.Util;

public class IdentifierSecurityProvider {
	public static final int ENCRYPT_DES = 0;
	public static final int ENCRYPT_DESEDE = 1;
	public static final int ENCRYPT_AES = 2;

	public Cipher getCipher(int algorithm, byte secretKey[], int opmode, byte[] iv) throws Exception {
		String keyAlg;
		String cipherAlg;
		KeySpec spec;
		if(algorithm == ENCRYPT_DES) {
			keyAlg = "DES";
			cipherAlg = "DES/CBC/PKCS5Padding";
			spec = new DESKeySpec(secretKey);
		}else if (algorithm == ENCRYPT_AES){
			keyAlg = "AES";
			cipherAlg = "AES/CBC/PKCS5Padding";
			if (secretKey.length > 16)
				secretKey = Util.substring(secretKey, 0, 16);
			spec = new SecretKeySpec(secretKey, "AES");
		} else if (algorithm == ENCRYPT_DESEDE){
			keyAlg = "DESede";
			cipherAlg = "DESede/CBC/PKCS5Padding";
			spec = new DESedeKeySpec(secretKey);
		}else {
			throw new Exception("无效的加密算法码: " + algorithm);
		}
		SecretKey key = null;
		if (spec instanceof SecretKeySpec)
			key = (SecretKeySpec) spec;
		else {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(keyAlg);
			key = factory.generateSecret(spec);
		}
		Cipher cipher = null;
		cipher = Cipher.getInstance(cipherAlg);
		if (iv != null) {
			cipher.init(opmode, key, new IvParameterSpec(iv));
		} else {
			cipher.init(opmode, key);
		}
		return cipher;
	}
}
