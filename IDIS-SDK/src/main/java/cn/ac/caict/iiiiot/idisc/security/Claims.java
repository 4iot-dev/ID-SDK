package cn.ac.caict.iiiiot.idisc.security;

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
    // 受发者即主体，格式为“index:identifier”,如“100：88.123”
    public String sub;
    // 过期时间，2年过期时间这样计算：System.currentTimeMillis() / 1000L + (oneYearInSeconds * 2)
    public Long exp;
    // 开始日期
    public Long nbf;
    // 发行时间
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
