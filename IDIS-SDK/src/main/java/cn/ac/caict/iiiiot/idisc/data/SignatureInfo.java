package cn.ac.caict.iiiiot.idisc.data;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import cn.ac.caict.iiiiot.idisc.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.idisc.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.idisc.security.Claims;
import cn.ac.caict.iiiiot.idisc.security.IdentifierValuesDigest;
import cn.ac.caict.iiiiot.idisc.security.IdentifierValuesDigests;
import cn.ac.caict.iiiiot.idisc.security.Permission;
import cn.ac.caict.iiiiot.idisc.security.gm.SM3Tool;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.Util;

/**
 *	1.签名所须原始数据
 *	2.签名具体操作流程 
 *	sub: 该JWT所面向的用户
 *	iss: 该JWT的签发者
 *	iat(issued at): 在什么时候签发的token
 *	exp(expires): token什么时候过期
 *	nbf(not before)：token在此时间之前不能被接收处理
 */
public class SignatureInfo {
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
    
    public SignatureInfo(PrivateKey prvKey, PublicKey pubKey,IdentifierValue[] values,String iss, String sub,Long exp,Long nbf,Long iat,String digestAlg){
    	this.iss = iss;
    	this.prvKey = prvKey;
    	this.pubKey = pubKey;
    	this.values = values;
    	this.sub = sub;
    	this.exp = exp;
    	this.nbf = nbf;
    	this.iat = iat;
    	this.digestAlg = digestAlg;
    }
    
    public SignatureInfo(PrivateKey prvKey, PublicKey pubKey,List<Permission> perms, String iss, String sub,Long exp,Long nbf,Long iat){
    	this.iss = iss;
    	this.pubKey = pubKey;
    	this.prvKey = prvKey;
    	this.perms = perms;
    	this.sub = sub;
    	this.exp = exp;
    	this.nbf = nbf;
    	this.iat = iat;
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
    	byte[] encodedHandleValue = MsgBytesConvertor.convertIdentifierValueToByte(value);
    	
    	try {
    		byte[] forDig = new byte[encodedHandleValue.length - VALUE_DIGEST_OFFSET];
    		System.arraycopy(encodedHandleValue, VALUE_DIGEST_OFFSET, forDig, 0, encodedHandleValue.length - VALUE_DIGEST_OFFSET);
    		System.out.println("[digestWithSM3]index=" + value.getIndex() + "的HandleValue的16进制数据：" + Util.bytesToHexString(forDig));
			byte[] digest = sm3.hash(forDig);
			System.out.println("[digestWithSM3]index=" + value.getIndex() + "的HandleValue做sm3摘要后的16进制数据：" + Util.bytesToHexString(digest));
	        result.digest = Base64.encodeBase64String(digest);
	        System.out.println("[digestWithSM3]index=" + value.getIndex() + "的HandleValue做sm3摘要后再做base64编码：" + result.digest);
	        result.index = value.getIndex();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed:handle value do digest wiht SM3!");
		}
    	return result;
    }
}
