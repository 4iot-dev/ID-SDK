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
import java.security.PublicKey;
import java.util.List;

public class Claims {
	
	// 描述证书权限
	public List<Permission> perms;
	// 指定标识值的摘要信息
    public IdentifierValuesDigests digests;
    // 证书公钥信息
    public PublicKey publicKey;
    // 颁发者，格式为“index:identifier”,如“100：88.123”
    public String iss;
    // 受发者即主体，格式为“index:identifier”,如“100：88.123”如果是签名则不需要有index，“88.123”即可
    public String sub;
    // 过期时间，2年过期时间这样计算：System.currentTimeMillis() / 1000L + (oneYearInSeconds * 2)
    public Long exp;
    // 生效时间
    public Long nbf;
    // 颁发时间
    public Long iat;
    
    public boolean isDateInRange(long nowInSeconds) {
        if (exp != null && nowInSeconds > exp) return false;
        if (nbf != null && nowInSeconds < nbf) return false;
        return true;
    }

    public boolean isSelfIssued() {
        return sub != null && sub.equals(iss);
    }
}
