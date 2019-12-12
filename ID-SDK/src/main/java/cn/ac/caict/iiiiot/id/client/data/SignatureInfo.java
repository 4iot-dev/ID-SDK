package cn.ac.caict.iiiiot.id.client.data;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.id.client.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.id.client.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.security.Claims;
import cn.ac.caict.iiiiot.id.client.security.IdentifierValuesDigest;
import cn.ac.caict.iiiiot.id.client.security.IdentifierValuesDigests;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.security.gm.SM3Tool;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.DateUtils;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

/**
 *	1.签名所须原始数据
 *	2.签名具体操作流程 
 *	sub: 该JWT所面向的用户
 *	iss: 该JWT的签发者
 *	iat(issued at): 在什么时候签发的token,即签发时间
 *	exp(expires): token什么时候过期，即过期时长，比如几个月，几年，转换为秒
 *	nbf(not before)：token在此时间之前不能被接收处理，即生效时间
 */
public class SignatureInfo {
	private static Log logger = IDLog.getLogger(SignatureInfo.class);
	private static String INDEX_ID_PATTERN = "\\d+:(.*)";
	private static String SHA256_PATTERN = "\\s*[S,s][H,h][A,a][\\s,-]?256\\s*";
	public List<Permission> perms;
	public String digestAlg;
	public ValueReference signer;
	public PrivateKey prvKey;
	public PublicKey pubKey;
	public IdentifierValue[] values;
    public String iss;
    public String sub;
    public Long exp;
    public Long nbf;
    public Long iat;
    
    private SignatureInfo(PrivateKey prvKey, IdentifierValue[] values,String iss, String sub,Long exp,Long nbf,Long iat,String digestAlg) throws IdentifierException{
    	this.iss = iss;
    	this.prvKey = prvKey;
    	this.values = values;
    	this.exp = exp;
    	this.nbf = nbf;
    	this.iat = iat;
    	Pattern r_alg = Pattern.compile(SHA256_PATTERN);
		Matcher m_alg = r_alg.matcher(digestAlg);
    	if (m_alg.matches()){
    		this.digestAlg = "SHA-256";
    	} else if("SM3".equalsIgnoreCase(digestAlg)){
    		this.digestAlg = "SM3";
    	} else {
    		throw new IdentifierException(ExceptionCommon.INVALID_PARM, "仅支持SHA256和SM3摘要算法");
    	}
    	if (isValidFormat(sub) && sub.indexOf(":")<sub.length()-1){
    		this.sub = sub.substring(sub.indexOf(":")+1);
    	} else {
    		this.sub = sub;
    	}
    }
    
    private SignatureInfo(PrivateKey prvKey, PublicKey pubKey,List<Permission> perms, String iss, String sub,Long exp,Long nbf,Long iat) throws IdentifierException{
		if (isValidFormat(iss))
			this.iss = iss;
		else
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "iss格式应为index:identifier");
		if (isValidFormat(sub))
			this.sub = sub;
		else
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "sub格式应为index:identifier");
    	this.pubKey = pubKey;
    	this.prvKey = prvKey;
    	this.perms = perms;
    	this.exp = exp;
    	this.nbf = nbf;
    	this.iat = iat;
    }
    
    public static SignatureInfo newSignatureInstance(PrivateKey prvKey, IdentifierValue[] values,String iss, String sub,String expirationTime,String notBefore,String issedAfterTime,String digestAlg) throws IdentifierException{
    	Long exp_end = DateUtils.parseString2Secs(expirationTime);
    	logger.info("签名过期时间" + expirationTime + "的秒数表示：" + exp_end);
    	Long iat = DateUtils.parseString2Secs(issedAfterTime);
    	logger.info("签名时间" + issedAfterTime + "的秒数表示：" + iat);
    	Long nbf = DateUtils.parseString2Secs(notBefore);
    	logger.info("签名生效时间" + notBefore + "的秒数表示：" + iat);
    	if(iss == null)
    		iss = "";
    	if(sub == null)
    		sub = "";
    	if(digestAlg == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "digestAlg摘要不能为空！");
    	if(prvKey == null)
    		throw new IdentifierException(ExceptionCommon.INVALID_PARM, "私钥不能为空！");
    	return new SignatureInfo(prvKey,values,iss,sub,exp_end,nbf,iat,digestAlg);
    }
    
    public static SignatureInfo newCertificationInstance(PrivateKey prvKey, PublicKey pubKey,List<Permission> perms, String iss, String sub,String expirationTime,String notBefore,String issedAfterTime) throws IdentifierException{
    	Long exp_end = DateUtils.parseString2Secs(expirationTime);
    	logger.info("证书过期时间：" + expirationTime + "的秒数表示：" + exp_end);
    	Long iat = DateUtils.parseString2Secs(issedAfterTime);
    	logger.info("颁发时间" + issedAfterTime + "的秒数表示：" + iat);
    	Long nbf = DateUtils.parseString2Secs(notBefore);
    	logger.info("证书生效时间" + notBefore + "的秒数表示：" + iat);
    	if(iss == null)
    		iss = "";
    	if(sub == null)
    		sub = "";
    	if(prvKey == null)
    		throw new IdentifierException(ExceptionCommon.INVALID_PARM, "私钥不能为空！");
    	return new SignatureInfo(prvKey, pubKey, perms, iss, sub, exp_end, nbf, iat);
    }
    
    public Claims getClaims() throws NoSuchAlgorithmException{
    	//获取基本数据
    	Claims claims = new Claims();
    	claims.sub = sub;
        claims.iss = iss;
        claims.iat = iat;
        claims.nbf = nbf;
        claims.exp = exp;
    	claims.digests = doDigests(values, digestAlg);
    	claims.publicKey = pubKey;
    	claims.perms = perms;
    	return claims;
    }
    private static final int VALUE_DIGEST_OFFSET = Common.FOUR_SIZE*2;
    public IdentifierValuesDigests doDigests(IdentifierValue[] values, String alg) throws NoSuchAlgorithmException{
    	if(values == null)
    		return null;
    	IdentifierValuesDigests allDigest = new IdentifierValuesDigests();
    	allDigest.alg = alg;
    	List<IdentifierValuesDigest> digest_array = new ArrayList<IdentifierValuesDigest>();
    	if("SHA-256".equals(alg)){
    		final MessageDigest digester = MessageDigest.getInstance(alg);
            for (IdentifierValue value : values) {
            	IdentifierValuesDigest valueOfDigest = doDigest(value, digester);
            	digest_array.add(valueOfDigest);
            }
    	} else if("SM3".equals(alg)) {
    		SM3Tool sm3 = new SM3Tool();
        	for (IdentifierValue value : values) {
        		IdentifierValuesDigest digest = digestWithSM3(value,sm3);
        		digest_array.add(digest);
            }
    	}
    	allDigest.digests = digest_array;
        return allDigest;
    }
    
    private IdentifierValuesDigest doDigest(IdentifierValue value, MessageDigest digester){
    	if(value == null)
    		return null;
    	IdentifierValuesDigest digest = new IdentifierValuesDigest();
    	digester.reset();
    	byte[] buf = new byte[BaseConvertor.calcStorageSize(value)];
        MsgBytesConvertor.convertIdentifierValueToByte(buf, 0, value);
        digester.update(buf, VALUE_DIGEST_OFFSET, buf.length - VALUE_DIGEST_OFFSET); 
        byte[] digestBytes = digester.digest();
        digest.digest = Base64.encodeBase64String(digestBytes);
        digest.index = value.index;
    	return digest;
    }
    
    private IdentifierValuesDigest digestWithSM3(IdentifierValue value,SM3Tool sm3){
    	if(value == null)
    		return null;
    	IdentifierValuesDigest result = new IdentifierValuesDigest();
    	byte[] encodedIV = MsgBytesConvertor.convertIdentifierValueToByte(value);
    	
    	try {
    		byte[] forDig = new byte[encodedIV.length - VALUE_DIGEST_OFFSET];
    		System.arraycopy(encodedIV, VALUE_DIGEST_OFFSET, forDig, 0, encodedIV.length - VALUE_DIGEST_OFFSET);
    		System.out.println("[digestWithSM3]index=" + value.getIndex() + "的IdentifierValue的16进制数据：" + Util.bytesToHexString(forDig));
			byte[] digest = sm3.hash(forDig);
			System.out.println("[digestWithSM3]index=" + value.getIndex() + "的IdentifierValue做sm3摘要后的16进制数据：" + Util.bytesToHexString(digest));
	        result.digest = Base64.encodeBase64String(digest);
	        System.out.println("[digestWithSM3]index=" + value.getIndex() + "的IdentifierValue做sm3摘要后再做base64编码：" + result.digest);
	        result.index = value.getIndex();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed:IdentifierValue do digest with SM3!");
		}
    	return result;
    }
    
    private boolean isValidFormat(String src){
    	Pattern r = Pattern.compile(INDEX_ID_PATTERN);
		Matcher m = r.matcher(src);
		return m.matches();
    }
}
