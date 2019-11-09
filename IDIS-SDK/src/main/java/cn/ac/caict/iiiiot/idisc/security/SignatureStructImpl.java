package cn.ac.caict.iiiiot.idisc.security;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.math.ec.ECPoint;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import cn.ac.caict.iiiiot.idisc.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.security.gm.SM2KeyPair;
import cn.ac.caict.iiiiot.idisc.security.gm.SM2Tool;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class SignatureStructImpl implements SignatureStruct {
	private final String hashAlg;
	private final String keyAlg;
	private final byte[] header;
	private final byte[] based64_Header;
	private final byte[] payload;
	private final byte[] based64_Payload;
	private final byte[] signature;
	private final byte[] based64_Signature;

	public SignatureStructImpl(String payload, PrivateKey privateKey) throws IdentifierException {
		this(Util.encodeString(payload), privateKey);
	}

	public SignatureStructImpl(byte[] payload, PrivateKey privateKey) throws IdentifierException {
		this.payload = payload;
		keyAlg = privateKey.getAlgorithm();
		if ("RSA".equals(keyAlg)) {
			hashAlg = "SHA256";
			header = Util.encodeString("{\"alg\":\"RS256\"}");
		} else if ("DSA".equals(keyAlg)) {
			hashAlg = "SHA1";
			header = Util.encodeString("{\"alg\":\"DS160\"}");
		} else if ("EC".equals(keyAlg)){
       	 	hashAlg = "SM3";
       	 	header = Util.encodeString("{\"alg\":\"SM2SM3\"}");
		}else {
			throw new IllegalArgumentException("未知算法： " + keyAlg);
		}
		based64_Header = Base64.encodeBase64URLSafe(header);
		based64_Payload = Base64.encodeBase64URLSafe(payload);
		try {
			if("EC".equals(keyAlg)){
				SM2Tool sm2Tool = new SM2Tool();
        		byte[] bDot = new byte[]{(byte)'.'};
        		byte[] signData = Util.join(based64_Header,bDot,based64_Payload);
        		String data = new String(signData);
        		String id = "1234567812345678";
        		BigInteger prvKey = ((BCECPrivateKey) privateKey).getD();
        		ECPoint pubkey = sm2Tool.G.multiply(prvKey).normalize();
        		cn.ac.caict.iiiiot.idisc.security.gm.SM2Tool.Signature sign = sm2Tool.sign(data, id, new SM2KeyPair(pubkey,prvKey));
        		//signature转为byte[]
        		byte[] bR = sign.r.toByteArray();
        		byte[] bS = sign.s.toByteArray();
        		signature = BaseConvertor.signatureFormat(bR,bS);
        		//将byte[]用Base64编码
        		based64_Signature = Base64.encodeBase64URLSafe(signature);
			} else {
				Signature sig = Signature.getInstance(hashAlg + "with" + keyAlg);
				sig.initSign(privateKey);
				sig.update(based64_Header);
				sig.update((byte) '.');
				sig.update(based64_Payload);
				signature = sig.sign();
				based64_Signature = Base64.encodeBase64URLSafe(signature);
			}
			
		} catch (Exception e) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, "构建签名数据失败");
		}
	}

	public SignatureStructImpl(byte[] data) throws IdentifierException {
		Gson gson = new Gson();
		String strData = Util.decodeString(data);
		SignatureJsonStruction signatureStruct = gson.fromJson(strData, SignatureJsonStruction.class);

		based64_Header = Util.encodeString(signatureStruct.signatures.get(0).header);
		header = Base64.decodeBase64(based64_Header);
		based64_Payload = Util.encodeString(signatureStruct.payload);
		payload = Base64.decodeBase64(based64_Payload);
		based64_Signature = Util.encodeString(signatureStruct.signatures.get(0).signature);
		signature = Base64.decodeBase64(based64_Signature);
		String algString = getAlgStringFromHeader(header);
		keyAlg = getKeyAlgFromAlg(algString);
		hashAlg = getHashAlgFromAlg(algString);
	}
	
	public String getPayloadString(){
		return Util.decodeString(payload);
	}

	@Override
	public byte[] getSignatureBytes() {
		byte[] result = Util.concat(based64_Header, new byte[] { '.' });
		result = Util.concat(result, based64_Payload);
		result = Util.concat(result, new byte[] { '.' });
		result = Util.concat(result, based64_Signature);
		return result;
	}

	@Override
	public String getSignatureString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.decodeString(based64_Header)).append('.').append(Util.decodeString(based64_Payload)).append('.')
				.append(Util.decodeString(based64_Signature));
		return sb.toString();
	}

	private static String getAlgStringFromHeader(byte[] header) throws IdentifierException {
		try {
			String alg = new JsonParser().parse(Util.decodeString(header)).getAsJsonObject().get("alg").getAsString();
			return alg;
		} catch (Exception e) {
			throw new IdentifierException(ExceptionCommon.JWT_PARSE_ERROR, "签名数据header解析异常");
		}
	}

	private static String getKeyAlgFromAlg(String alg) throws IdentifierException {
		if (alg.startsWith("RS"))
			return "RSA";
		else if (alg.startsWith("DS"))
			return "DSA";
		throw new IdentifierException(ExceptionCommon.JWT_PARSE_ERROR, "签名数据header解析异常");
	}

	private static String getHashAlgFromAlg(String alg) throws IdentifierException {
		if (alg.endsWith("256"))
			return "SHA256";
		else if (alg.endsWith("160") || alg.endsWith("128") || alg.equals("DSA") || alg.equals("DS"))
			return "SHA1";
		else if (alg.endsWith("384"))
			return "SHA384";
		else if (alg.endsWith("512"))
			return "SHA512";
		throw new IdentifierException(ExceptionCommon.JWT_PARSE_ERROR, "签名数据header解析异常");
	}
}
