package cn.ac.caict.iiiiot.id.client.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

import cn.ac.caict.iiiiot.id.client.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.id.client.core.BaseMessage;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.security.IdentifierSecurityProvider;
import cn.hutool.crypto.SecureUtil;

public abstract class Util {
	private static final String PEM = "pem";
	private static final String BIN = "bin";
	private static final String STARTPATTERN = "^\\s*-----BEGIN (.*) KEY-----\\s*$";
	private static final String ENDPATTERN = "^\\s*-----END (.*) KEY-----\\s*$";
	public static final String HMACSHA256 = "HmacSHA256";
	public static final String SHA_256 = "SHA-256";
	public static final String HMACSHA1 = "HmacSHA1";
	private static final char[] HEX_VALUES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static byte[] doSHA256Digest(byte[]... bufs) throws IdentifierException {
		MessageDigest digest = getSHA256Digest();
		for (byte[] buf : bufs)
			digest.update(buf);
		return digest.digest();
	}

	public static final byte[] doSHA1Digest(byte[]... bufs) throws IdentifierException {
		MessageDigest digest = getSHA1Digest();
		for (byte[] buf : bufs)
			digest.update(buf);
		return digest.digest();
	}

	public static final byte[] doMD5Digest(byte[]... bufs) throws IdentifierException {
		MessageDigest digest = getMD5Digest();
		for (byte[] buf : bufs)
			digest.update(buf);
		return digest.digest();
	}

	private static final MessageDigest getSHA256Digest() throws IdentifierException {
		try {
			return MessageDigest.getInstance(SHA_256);
		} catch (NoSuchAlgorithmException e) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_CRYPTO_PROVIDER, "未找到SHA-256算法", e);
		}
	}

	public static byte[] getHashAlgIdFromSigId(String signatureAlgorithm) throws IdentifierException {
		if (signatureAlgorithm.startsWith("SHA1"))
			return Common.HASH_ALG_SHA1;
		else if (signatureAlgorithm.startsWith("SHA256"))
			return Common.HASH_ALG_SHA256;
		else if (signatureAlgorithm.startsWith("MD5"))
			return Common.HASH_ALG_MD5;
		throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE,
				"未知签名算法: " + signatureAlgorithm);
	}

	public static String getDefaultSigId(String algorithm) {
		if ("DSA".equals(algorithm))
			return "SHA1withDSA";
		else
			return "SHA256with" + algorithm;
	}

	public static String getDefaultSigId(String algorithm, BaseMessage message) throws IdentifierException {
		return getDefaultSigId(algorithm);
	}

	public static byte[] doPBKDF2(byte[] password, byte[] salt, int iterations, int length)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		char[] charPassword = new char[password.length];
		for (int i = 0; i < password.length; i++) {
			charPassword[i] = (char) (password[i] & 0xFF);
		}
		KeySpec spec = new PBEKeySpec(charPassword, salt, iterations, length);
		SecretKey tmp = factory.generateSecret(spec);
		return tmp.getEncoded();
	}

	private static final MessageDigest getSHA1Digest() throws IdentifierException {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_CRYPTO_PROVIDER, "没有找到SHA1算法", e);
		}
	}

	private static final synchronized MessageDigest getMD5Digest() throws IdentifierException {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_CRYPTO_PROVIDER, "没有找到MD-5算法", e);
		}
	}

	public static final byte[] doDigest(byte digestType, byte[]... bufs) throws IdentifierException {
		switch (digestType) {
		case Common.HASH_CODE_SHA256:
			return doSHA256Digest(bufs);
		case Common.HASH_CODE_SHA1:
			return doSHA1Digest(bufs);
		case Common.HASH_CODE_MD5:
		case Common.HASH_CODE_MD5_OLD_FORMAT:
			return doMD5Digest(bufs);
		default:
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE,
					"无效的hash类型: " + ((int) digestType));
		}
	}

	public static final byte[] doDigest(byte[] digestType, byte[]... bufs) throws IdentifierException {
		if (digestType.length == 1)
			return doDigest(digestType[0], bufs);
		if (Util.equalsBytes(digestType, Common.HASH_ALG_SHA1)
				|| Util.equalsBytes(digestType, Common.HASH_ALG_SHA1_ALTERNATE))
			return doDigest(Common.HASH_CODE_SHA1, bufs);
		else if (Util.equalsBytes(digestType, Common.HASH_ALG_MD5))
			return doDigest(Common.HASH_CODE_MD5, bufs);
		else if (Util.equalsBytes(digestType, Common.HASH_ALG_SHA256)
				|| Util.equalsBytes(digestType, Common.HASH_ALG_SHA256_ALTERNATE))
			return doDigest(Common.HASH_CODE_SHA256, bufs);
		else
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE,
					"无效的hash类型: " + Util.decodeString(digestType));
	}

	public static String rfcIpPortRepresentation(InetAddress addr, int port) {
		if (addr instanceof Inet4Address) {
			return addr.getHostAddress() + ":" + port;
		} else
			return "[" + Util.rfcIpRepresentation(addr) + "]:" + port;
	}

	private static int[] intsFromByteIPv6Address(byte[] ipv6Address) {
		if (ipv6Address.length != 16)
			return null;
		int integerAddress[] = new int[8];
		for (int i = 0; i < 16; i += 2) {
			integerAddress[i / 2] = ((ipv6Address[i] & 0xFF) << 8) | (ipv6Address[i + 1] & 0xFF);
		}
		return integerAddress;
	}

	private static String intIPv6Address2String(int[] intInet6Address) {
		StringBuilder sb = new StringBuilder();
		boolean previousWasZero = false;
		int currentZeros = 0;
		int currentZeroStartIndex = 0;
		int largestZeros = 0;
		int largestZeroStartIndex = -1;

		for (int i = 0; i < 8; i++) {
			if (intInet6Address[i] == 0) {
				if (!previousWasZero) {
					currentZeroStartIndex = i;
					previousWasZero = true;
				}
				currentZeros += 1;
			} else {
				previousWasZero = false;
				if (currentZeros > largestZeros) {
					largestZeroStartIndex = currentZeroStartIndex;
					largestZeros = currentZeros;
				}
				currentZeros = 0;
			}

		}
		if (largestZeros == 1) {
			largestZeroStartIndex = -1;
		}
		for (int i = 0; i < 8; i++) {
			if (i != largestZeroStartIndex) {
				sb.append(Integer.toHexString(intInet6Address[i]));
				if (i < 8 - 1) {
					sb.append(":");
				}
			} else {
				if (i == 0)
					sb.append(":");
				i += largestZeros - 1;
				sb.append(":");
			}
		}
		return sb.toString();
	}

	public static String rfcIpRepresentation(byte[] ipv6Address) {
		if (ipv6Address == null)
			return null;
		int[] ints = intsFromByteIPv6Address(ipv6Address);
		if (ints == null) {
			if (ipv6Address.length == 4)
				return (ipv6Address[0] & 0xFF) + "." + (ipv6Address[1] & 0xFF) + "." + (ipv6Address[2] & 0xFF) + "."
						+ (ipv6Address[3] & 0xFF);
			throw new IllegalArgumentException();
		}
		return intIPv6Address2String(ints);
	}

	public static String rfcIpRepresentation(InetAddress addr) {
		if (addr == null)
			return null;
		if (addr instanceof Inet4Address) {
			return addr.getHostAddress();
		}

		String hostAddress = addr.getHostAddress();
		String IPv6_addr = hostAddress;
		String scope_id = null;
		int sep = hostAddress.indexOf('%');
		if (sep >= 0) {
			IPv6_addr = hostAddress.substring(0, sep);
			scope_id = hostAddress.substring(sep + 1);
		}
		int[] intAddress = new int[8];
		String[] addrParts = IPv6_addr.split(":");
		boolean bRet = false;
		if (addrParts.length != 8) {
			bRet = true;
		}
		for (int i = 0; i < 8; i++) {
			if (addrParts[i].length() == 0) {
				bRet = true;
			}
			try {
				intAddress[i] = Integer.parseInt(addrParts[i], 16);
			} catch (NumberFormatException e) {
				bRet = true;
			}
		}
		if (bRet)
			return hostAddress;
		String ipStrng = intIPv6Address2String(intAddress);
		if (scope_id != null)
			return ipStrng + "%" + scope_id;
		return ipStrng;
	}

	public static boolean startsWithCaseInsensitive(String s1, String s2) {
		return startsWithCaseInsensitive(encodeString(s1), encodeString(s2));
	}

	public static final byte[] substring(byte[] b, int i1) {
		return substring(b, i1, b.length);
	}

	public static final byte[] substring(byte[] b, int i1, int i2) {
		byte rb[] = new byte[i2 - i1];
		System.arraycopy(b, i1, rb, 0, i2 - i1);
		return rb;
	}

	public static final int indexOf(byte[] b, byte ch) {
		for (int i = 0; i < b.length; i++) {
			if (b[i] == ch)
				return i;
		}
		return -1;
	}

	public static final byte DIFFER = 'A' - 'a';

	public static final byte[] upperCase(byte[] b) {
		if (b == null || b.length == 0)
			return new byte[0];
		int sz = b.length;
		byte b2[] = new byte[sz];
		System.arraycopy(b, 0, b2, 0, sz);

		for (int i = sz - 1; i >= 0; i--) {
			if (b2[i] >= 'a' && b2[i] <= 'z')
				b2[i] += DIFFER;
		}
		return b2;
	}

	public static String upperCase(String s) {
		if (s == null)
			return "";
		return decodeString(upperCase(encodeString(s)));
	}

	public static final byte[] upperCaseInPlace(byte[] b) {
		if (b == null || b.length == 0)
			return b;
		for (int i = 0; i < b.length; i++) {
			if (b[i] >= 'a' && b[i] <= 'z')
				b[i] += DIFFER;
		}
		return b;
	}

	public static final byte[] upperCasePrefix(byte[] b) {
		if (b == null || b.length == 0)
			return new byte[0];
		if (startsWith(b, Common.GLOBAL_NA_PREFIX))
			return upperCase(b);
		int sz = b.length;
		byte b2[] = new byte[sz];
		System.arraycopy(b, 0, b2, 0, sz);

		boolean inPrefix = true;
		for (int i = 0; i < sz; i++) {
			if (inPrefix && b2[i] >= 'a' && b2[i] <= 'z')
				b2[i] += DIFFER;
			if (b2[i] == '/')
				inPrefix = false;
		}
		return b2;
	}

	public static String upperCasePrefix(String s) {
		if (s == null)
			return "";
		return decodeString(upperCasePrefix(encodeString(s)));
	}

	public static final byte[] upperCasePrefixInPlace(byte b[]) {
		if (b == null || b.length == 0)
			return b;
		if (startsWith(b, Common.GLOBAL_NA_PREFIX))
			return upperCaseInPlace(b);
		boolean inPrefix = true;
		for (int i = 0; i < b.length; i++) {
			if (inPrefix && b[i] >= 'a' && b[i] <= 'z')
				b[i] += DIFFER;
			if (b[i] == '/')
				inPrefix = false;
		}
		return b;
	}

	public static final boolean equalsCaseInsensitive(byte b1[], byte b2[]) {
		if (b1 == null && b2 == null)
			return true;
		if (b1 == null || b2 == null)
			return false;
		return equalsCaseInsensitive(b1, b1.length, b2, b2.length);
	}

	public static boolean equalsCaseInsensitive(String s1, String s2) {
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null || s2 == null)
			return false;
		return equalsCaseInsensitive(encodeString(s1), encodeString(s2));
	}

	public static final boolean equalsCaseInsensitive(byte b1[], int b1Len, byte b2[], int b2Len) {
		if (b1 == null && b2 == null)
			return true;
		if (b1 == null || b2 == null)
			return false;
		if (b1Len != b2Len || b1Len > b1.length || b2Len > b2.length)
			return false;

		byte byte1, byte2;
		for (int i = 0; i < b1Len; i++) {
			byte1 = b1[i];
			byte2 = b2[i];
			if (byte1 == byte2)
				continue;
			if (byte1 >= 'a' && byte1 <= 'z')
				byte1 += DIFFER;
			if (byte2 >= 'a' && byte2 <= 'z')
				byte2 += DIFFER;
			if (byte1 != byte2)
				return false;
		}
		return true;
	}

	public static final boolean equalsPrefixCaseInsensitive(byte b1[], byte b2[]) {
		if (b1 == null && b2 == null)
			return true;
		if (b1 == null || b2 == null)
			return false;
		return equalsPrefixCaseInsensitive(b1, b1.length, b2, b2.length);
	}

	public static final boolean equalsPrefixCaseInsensitive(String s1, String s2) {
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null || s2 == null)
			return false;
		return equalsPrefixCaseInsensitive(encodeString(s1), encodeString(s2));
	}

	public static final boolean equalsPrefixCaseInsensitive(byte b1[], int b1Len, byte b2[], int b2Len) {
		if (b1 == null && b2 == null)
			return true;
		if (b1 == null || b2 == null)
			return false;
		if (b1Len != b2Len || b1Len > b1.length || b2Len > b2.length)
			return false;

		boolean global = startsWith(b1, Common.GLOBAL_NA_PREFIX);

		byte byte1, byte2;
		boolean inPrefix = true;
		for (int i = 0; i < b1Len; i++) {
			byte1 = b1[i];
			byte2 = b2[i];
			if (byte1 == '/' && !global)
				inPrefix = false;
			if (byte1 == byte2)
				continue;
			if (inPrefix && byte1 >= 'a' && byte1 <= 'z')
				byte1 += DIFFER;
			if (inPrefix && byte2 >= 'a' && byte2 <= 'z')
				byte2 += DIFFER;
			if (byte1 != byte2)
				return false;
		}
		return true;
	}

	public static final boolean startsWithCaseInsensitive(byte[] b1, byte[] b2) {
		if (b1 == null || b2 == null)
			return false;
		if (b1.length < b2.length)
			return false;
		byte byte1, byte2;
		for (int i = 0; i < b2.length; i++) {
			byte1 = b1[i];
			byte2 = b2[i];
			if (byte1 == byte2)
				continue;
			if (byte1 >= 'a' && byte1 <= 'z')
				byte1 += DIFFER;
			if (byte2 >= 'a' && byte2 <= 'z')
				byte2 += DIFFER;
			if (byte1 != byte2)
				return false;
		}
		return true;
	}

	public static final boolean startsWith(byte[] b1, byte[] b2) {
		if (b1.length < b2.length)
			return false;
		for (int i = 0; i < b2.length; i++)
			if (b1[i] != b2[i])
				return false;
		return true;
	}

	public static final boolean equalsBytes(byte[] b1, byte[] b2) {
		if (b1 == null && b2 == null)
			return true;
		if (b1 == null || b2 == null)
			return false;
		if (b1.length != b2.length)
			return false;
		for (int i = 0; i < b1.length; i++)
			if (b1[i] != b2[i])
				return false;
		return true;
	}

	public static final boolean looksLikeBinary(byte buf[]) {
		if (buf == null)
			return true;
		for (int i = 0; i < buf.length; i++) {
			byte b = buf[i];
			if (b >= 0x09 && b <= 0x13)
				continue;
			if ((b >= 0x00 && b < 0x20) || b == 0x7F)
				return true;
		}
		return false;
	}

	public static final byte[] duplicateByteArray(byte buf[]) {
		if (buf == null)
			return null;
		byte newbuf[] = new byte[buf.length];
		System.arraycopy(buf, 0, newbuf, 0, newbuf.length);
		return newbuf;
	}

	public static final String decodeHexString(byte buf[], int offset, int len, boolean bFormat) {
		if (buf == null || buf.length <= 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = offset; i < offset + len; i++) {
			if (bFormat && i > 0 && (i % 16) == 0)
				sb.append('\n');
			sb.append(HEX_VALUES[(buf[i] & 0xF0) >>> 4]);
			sb.append(HEX_VALUES[(buf[i] & 0xF)]);
		}
		return sb.toString();
	}

	public static final String decodeHexString(byte[] buf, boolean bFormat) {
		return decodeHexString(buf, 0, buf.length, bFormat);
	}

	public static final byte[] encodeString(String s) {
		if (s == null)
			return null;
		try {
			return s.getBytes(Common.TEXT_ENCODING);
		} catch (Exception e) {
			System.err.println(e);
		}
		return s.getBytes();
	}

	public static final String decodeString(byte buf[]) {
		if (buf == null || buf.length == 0)
			return "";
		try {
			return new String(buf, Common.TEXT_ENCODING);
		} catch (Exception e) {
			System.err.println(e);
		}
		return new String(buf);
	}

	public static final String decodeString(byte buf[], int offset, int len) {
		if (buf == null || buf.length == 0)
			return "";
		try {
			return new String(buf, offset, len, Common.TEXT_ENCODING);
		} catch (Exception e) {
			System.err.println(e);
		}
		return new String(buf, offset, len);
	}

	/**
	 * 字节数组连接
	 * 
	 * @param first
	 *            第一组字节
	 * @param second
	 *            第二组字节
	 * @return 第一组和第二组连接后的结果字节数组
	 */
	public static byte[] concat(byte[] first, byte[] second) {
		if (second.length == 0)
			return first;
		if (first.length == 0)
			return second;
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/*************************************************** 公私钥操作 *************************************************************/
	public static PrivateKey getPrivateKeyFromBytes(byte pkBuf[])
			throws IOException, IdentifierException, InvalidKeySpecException {
		return getPrivateKeyFromBytes(pkBuf, 0);
	}

	public static PublicKey getPublicKeyFromBytes(byte pkBuf[]) throws Exception {
		return getPublicKeyFromBytes(pkBuf, 0);
	}

	public static PublicKey getPublicKeyFromBytes(byte pkBuf[], int offset) throws Exception {
		byte[] keyType = BaseConvertor.readByteArray(pkBuf, offset);
		offset += Common.FOUR_SIZE + keyType.length;
		int flags = BaseConvertor.read2Bytes(pkBuf, offset); // 暂不使用
		offset += Common.TWO_SIZE;
		if (Util.equalsBytes(keyType, Common.KEY_ENCODING_DSA_PUBLIC)) {
			byte[] q = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + q.length;
			byte[] p = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + p.length;
			byte[] g = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + g.length;
			byte[] y = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + y.length;

			DSAPublicKeySpec keySpec = new DSAPublicKeySpec(new BigInteger(1, y), new BigInteger(1, p),
					new BigInteger(1, q), new BigInteger(1, g));
			try {
				KeyFactory dsaKeyFactory = KeyFactory.getInstance("DSA");
				return dsaKeyFactory.generatePublic(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持DSA加密算法", e);
			}
		} else if (Util.equalsBytes(keyType, Common.KEY_ENCODING_RSA_PUBLIC)) {
			byte[] ex = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + ex.length;
			byte[] m = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + m.length;
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(1, m), new BigInteger(1, ex));
			try {
				KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
				return rsaKeyFactory.generatePublic(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持RSA加密算法", e);
			}
		} else if (Util.equalsBytes(keyType, Common.KEY_ENCODING_DH_PUBLIC)) {

			byte[] y = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + y.length;
			byte[] p = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + p.length;
			byte[] g = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + g.length;
			DHPublicKeySpec keySpec = new DHPublicKeySpec(new BigInteger(1, y), new BigInteger(1, p),
					new BigInteger(1, g));
			try {
				KeyFactory dhKeyFactory = KeyFactory.getInstance("DiffieHellman");
				return dhKeyFactory.generatePublic(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持DH加密算法", e);
			}
		}
		throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE,
				"未知公钥格式: \"" + Util.decodeString(keyType) + '"');
	}

	public static PrivateKey getPrivateKeyFromBytes(byte pkBuf[], int offset)
			throws IOException, IdentifierException, InvalidKeySpecException {
		byte[] keyType = BaseConvertor.readByteArray(pkBuf, offset);
		offset += Common.FOUR_SIZE + keyType.length;
		if (Util.equalsBytes(keyType, Common.KEY_ENCODING_DSA_PRIVATE)) {
			byte[] x = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + x.length;
			byte[] p = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + p.length;
			byte[] q = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + q.length;
			byte[] g = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + g.length;
			DSAPrivateKeySpec keySpec = new DSAPrivateKeySpec(new BigInteger(1, x), new BigInteger(1, p),
					new BigInteger(1, q), new BigInteger(1, g));
			try {
				KeyFactory dsaKeyFactory = KeyFactory.getInstance("DSA");
				return dsaKeyFactory.generatePrivate(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持DSA加密算法", e);
			}
		} else if (Util.equalsBytes(keyType, Common.KEY_ENCODING_RSA_PRIVATE)) {
			byte[] m = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + m.length;
			byte[] exp = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + exp.length;
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(1, m), new BigInteger(1, exp));
			try {
				KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
				return rsaKeyFactory.generatePrivate(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持RSA加密算法", e);
			}
		} else if (Util.equalsBytes(keyType, Common.KEY_ENCODING_RSACRT_PRIVATE)) {
			byte[] n = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + n.length;
			byte[] pubEx = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + pubEx.length;
			byte[] ex = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + ex.length;
			byte[] p = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + p.length;
			byte[] q = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + q.length;
			byte[] exP = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + exP.length;
			byte[] exQ = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + exQ.length;
			byte[] coeff = BaseConvertor.readByteArray(pkBuf, offset);
			offset += Common.FOUR_SIZE + coeff.length;
			RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(new BigInteger(1, n), new BigInteger(1, pubEx),
					new BigInteger(1, ex), new BigInteger(1, p), new BigInteger(1, q), new BigInteger(1, exP),
					new BigInteger(1, exQ), new BigInteger(1, coeff));
			try {
				KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
				return rsaKeyFactory.generatePrivate(keySpec);
			} catch (NoSuchAlgorithmException e) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR, "不支持RSA加密算法", e);
			}
		}
		throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE,
				"未知格式私钥: \"" + Util.decodeString(keyType) + '"');
	}

	public static byte[] decrypt(byte ciphertext[], byte secretKey[]) throws Exception {
		IdentifierSecurityProvider cryptoProvider = new IdentifierSecurityProvider();
		int encryptionType = BaseConvertor.read4Bytes(ciphertext, 0);
		switch (encryptionType) {
		case Common.ENCRYPT_DES_CBC_PKCS5:
			secretKey = doMD5Digest(secretKey);
			try {
				byte[] iv = Util.substring(ciphertext, 4, 12);
				ciphertext = Util.substring(ciphertext, 12);
				Cipher decryptCipher = cryptoProvider.getCipher(IdentifierSecurityProvider.ENCRYPT_DES, secretKey,
						Cipher.DECRYPT_MODE, iv);
				return decryptCipher.doFinal(ciphertext, 0, ciphertext.length);
			} catch (Exception e) {
				throw new Exception("无法解码");
			}
		case Common.ENCRYPT_PBKDF2_DESEDE_CBC_PKCS5:
			try {
				int offset = 4;
				byte[] salt = BaseConvertor.readByteArray(ciphertext, offset);
				offset += Common.FOUR_SIZE + salt.length;
				int iterations = BaseConvertor.read4Bytes(ciphertext, offset);
				offset += Common.FOUR_SIZE;
				int keyLength = BaseConvertor.read4Bytes(ciphertext, offset);
				offset += Common.FOUR_SIZE;
				secretKey = doPBKDF2(secretKey, salt, iterations, keyLength);
				byte[] iv = BaseConvertor.readByteArray(ciphertext, offset);
				offset += Common.FOUR_SIZE + iv.length;
				ciphertext = BaseConvertor.readByteArray(ciphertext, offset);
				Cipher decryptCipher = cryptoProvider.getCipher(IdentifierSecurityProvider.ENCRYPT_DESEDE, secretKey,
						Cipher.DECRYPT_MODE, iv);
				return decryptCipher.doFinal(ciphertext, 0, ciphertext.length);
			} catch (Exception e) {
				throw new Exception("无法解密");
			}
		case Common.ENCRYPT_PBKDF2_AES_CBC_PKCS5:
			try {
				int offset = 4;
				byte[] salt = BaseConvertor.readByteArray(ciphertext, offset);
				offset += Common.FOUR_SIZE + salt.length;
				int iterations = BaseConvertor.read4Bytes(ciphertext, offset);
				offset += Common.FOUR_SIZE;
				int keyLength = BaseConvertor.read4Bytes(ciphertext, offset);
				offset += Common.FOUR_SIZE;
				secretKey = doPBKDF2(secretKey, salt, iterations, keyLength);
				byte[] iv = BaseConvertor.readByteArray(ciphertext, offset);
				offset += Common.FOUR_SIZE + iv.length;
				ciphertext = BaseConvertor.readByteArray(ciphertext, offset);
				Cipher decryptCipher = cryptoProvider.getCipher(IdentifierSecurityProvider.ENCRYPT_AES, secretKey,
						Cipher.DECRYPT_MODE, iv);
				return decryptCipher.doFinal(ciphertext, 0, ciphertext.length);
			} catch (Exception e) {
				throw new Exception("Unable to decrypt");
			}
		case Common.ENCRYPT_NONE:
			byte cleartext[] = new byte[ciphertext.length - Common.FOUR_SIZE];
			System.arraycopy(ciphertext, Common.FOUR_SIZE, cleartext, 0, cleartext.length);
			return cleartext;
		default:
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE, "未知的加密类型码: " + encryptionType);
		}

	}

	public static byte[] getBytesFromPrivateKey(PrivateKey key) throws Exception {
		if (key instanceof DSAPrivateKey) {
			DSAPrivateKey dsaKey = (DSAPrivateKey) key;
			byte[] x = dsaKey.getX().toByteArray();
			DSAParams params = dsaKey.getParams();
			byte[] p = params.getP().toByteArray();
			byte[] q = params.getQ().toByteArray();
			byte[] g = params.getG().toByteArray();
			byte[] enc = new byte[Common.FOUR_SIZE * 5 + Common.KEY_ENCODING_DSA_PRIVATE.length + x.length + p.length
					+ q.length + g.length];
			int offset = 0;
			offset += BaseConvertor.writeByteArray(enc, offset, Common.KEY_ENCODING_DSA_PRIVATE);
			offset += BaseConvertor.writeByteArray(enc, offset, x);
			offset += BaseConvertor.writeByteArray(enc, offset, p);
			offset += BaseConvertor.writeByteArray(enc, offset, q);
			offset += BaseConvertor.writeByteArray(enc, offset, g);
			return enc;
		} else if (key instanceof RSAPrivateKey) {
			RSAPrivateKey rsaKey = (RSAPrivateKey) key;
			if (rsaKey instanceof RSAPrivateCrtKey) {
				RSAPrivateCrtKey rsacrtKey = (RSAPrivateCrtKey) rsaKey;
				byte[] x = rsacrtKey.getModulus().toByteArray();
				byte[] ex = rsacrtKey.getPrivateExponent().toByteArray();
				byte[] pubEx = rsacrtKey.getPublicExponent().toByteArray();
				byte[] p = rsacrtKey.getPrimeP().toByteArray();
				byte[] q = rsacrtKey.getPrimeQ().toByteArray();
				byte[] exP = rsacrtKey.getPrimeExponentP().toByteArray();
				byte[] exQ = rsacrtKey.getPrimeExponentQ().toByteArray();
				byte[] coeff = rsacrtKey.getCrtCoefficient().toByteArray();

				byte enc[] = new byte[Common.FOUR_SIZE * 9 + Common.KEY_ENCODING_RSACRT_PRIVATE.length + x.length
						+ ex.length + pubEx.length + p.length + q.length + exP.length + exQ.length + coeff.length];
				int offset = 0;
				offset += BaseConvertor.writeByteArray(enc, offset, Common.KEY_ENCODING_RSACRT_PRIVATE);
				offset += BaseConvertor.writeByteArray(enc, offset, x);
				offset += BaseConvertor.writeByteArray(enc, offset, pubEx);
				offset += BaseConvertor.writeByteArray(enc, offset, ex);
				offset += BaseConvertor.writeByteArray(enc, offset, p);
				offset += BaseConvertor.writeByteArray(enc, offset, q);
				offset += BaseConvertor.writeByteArray(enc, offset, exP);
				offset += BaseConvertor.writeByteArray(enc, offset, exQ);
				offset += BaseConvertor.writeByteArray(enc, offset, coeff);
				return enc;
			} else {
				byte[] x = rsaKey.getModulus().toByteArray();
				byte[] y = rsaKey.getPrivateExponent().toByteArray();
				byte[] enc = new byte[Common.FOUR_SIZE * 3 + Common.KEY_ENCODING_RSA_PRIVATE.length + x.length
						+ y.length];
				int offset = 0;
				offset += BaseConvertor.writeByteArray(enc, offset, Common.KEY_ENCODING_RSA_PRIVATE);
				offset += BaseConvertor.writeByteArray(enc, offset, x);
				offset += BaseConvertor.writeByteArray(enc, offset, y);
				return enc;
			}
		} else {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE, "未知的私钥类型: \"" + key + '"');
		}
	}

	public static PublicKey getPublicKeyFromFile(String filename) throws Exception {
		if(filename == null)
			return null;
		File pubKeyFile = new File(filename);
		if (!pubKeyFile.exists()) {
			System.out.println(filename + "文件不存在!");
			return null;
		}
		String fileName = pubKeyFile.getName();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (BIN.equalsIgnoreCase(suffix)) {
			return getPublicKeyFromBinFile(pubKeyFile);
		} else if (PEM.equalsIgnoreCase(suffix)) {
			return getPublicKeyKeyFromPemFile(pubKeyFile);
		} else {
			System.out.println("不支持的公钥文件格式：" + suffix);
			return null;
		}
	}

	private static PublicKey getPublicKeyFromBinFile(File pubKeyFile) throws Exception {
		FileInputStream in = new FileInputStream(pubKeyFile);
		byte buf[] = new byte[(int) pubKeyFile.length()];
		try {
			int r, n = 0;
			while ((r = in.read(buf, n, buf.length - n)) > 0)
				n += r;
		} finally {
			in.close();
		}
		return getPublicKeyFromBytes(buf, 0);
	}

	private static PublicKey getPublicKeyKeyFromPemFile(File pubKeyFile) throws Exception {
		InputStream in = null;
		int fLen = (int) pubKeyFile.length();
		byte[] strBuffer = new byte[fLen];
		try {
			in = new FileInputStream(pubKeyFile);
			in.read(strBuffer, 0, fLen);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null)
				in.close();
		}
		StringReader reader = new StringReader(new String(strBuffer));
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		StringBuilder contentsBase64 = new StringBuilder();
		String type = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().isEmpty() || line.isEmpty())
					continue;
				if (type == null) {
					Pattern typePattern = Pattern.compile(STARTPATTERN);
					Matcher matcher = typePattern.matcher(line);
					if (matcher.matches())
						type = matcher.group(1);
					else
						type = "";
					System.out.println("type=" + type);
					continue;
				}
				Pattern endPattern = Pattern.compile(ENDPATTERN);
				if (endPattern.matcher(line).matches())
					break;
				contentsBase64.append(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			in.close();
			return null;
		}
		System.out.println("contentsBase64" + contentsBase64);
		byte[] bytes = Base64.decodeBase64(contentsBase64.toString());
		boolean encrypted = "PUBLIC".equals(type);
		if (!encrypted && !"PUBLIC".equals(type)) {
			System.out.println("文件起始应该是\"-----BEGIN PUBLIC KEY-----\"");
			in.close();
			return null;
		}
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			try {
				return KeyFactory.getInstance("RSA").generatePublic(keySpec);
			} catch (InvalidKeySpecException e) {
				return KeyFactory.getInstance("DSA").generatePublic(keySpec);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		} catch (InvalidKeySpecException e) {
			try {
				return SecureUtil.generatePublicKey("SM2", bytes);
			} catch (Exception ex) {
				throw new Exception("RSA和DSA、SM2格式的公钥生成器都无法生成公钥", e);
			}
		} finally {
			in.close();
		}
	}

	public static PrivateKey getPrivateKeyFromFile(String privakeyFilePath, String password) {
		System.out.println("私钥文件路径：" + privakeyFilePath);
		if(privakeyFilePath == null)
			return null;
		try {
			File privKeyFile = new File(privakeyFilePath);
			if (!privKeyFile.exists()) {
				System.out.println(privakeyFilePath + "文件不存在!");
				return null;
			}
			String fileName = privKeyFile.getName();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (BIN.equalsIgnoreCase(suffix)) {
				return getPrivateKeyFromBinFile(privKeyFile, password);
			} else if (PEM.equalsIgnoreCase(suffix)) {
				return getPrivateKeyFromPemFile(privKeyFile, password);
			} else {
				System.out.println("不支持的私钥文件格式：" + suffix);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	private static PrivateKey getPrivateKeyFromBinFile(File privKeyFile, String password) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		FileInputStream fin = new FileInputStream(privKeyFile);
		try {
			int r = 0;
			while ((r = fin.read(buf)) >= 0)
				bout.write(buf, 0, r);
		} finally {
			fin.close();
		}
		buf = bout.toByteArray();
		byte[] passphrase = encodeString(password);
		buf = decrypt(buf, passphrase);
		return getPrivateKeyFromBytes(buf, 0);
	}

	private static PrivateKey getPrivateKeyFromPemFile(File privKeyFile, String password) {
		InputStream in = null;
		try {
			in = new FileInputStream(privKeyFile);
			int flen = (int) privKeyFile.length();
			byte[] strBuffer = new byte[flen];
			try {
				in.read(strBuffer, 0, flen);
			} catch (IOException e) {
				e.printStackTrace();
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return null;
			}
			StringReader reader = new StringReader(new String(strBuffer));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			StringBuilder contentsBase64 = new StringBuilder();
			String type = null;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					if (line.trim().isEmpty() || line.isEmpty())
						continue;
					if (type == null) {
						Pattern typePattern = Pattern.compile(STARTPATTERN);
						Matcher matcher = typePattern.matcher(line);
						if (matcher.matches())
							type = matcher.group(1);
						else
							type = "";
						System.out.println("type=" + type);
						continue;
					}
					Pattern endPattern = Pattern.compile(ENDPATTERN);
					if (endPattern.matcher(line).matches())
						break;
					contentsBase64.append(line);
				}
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			System.out.println("contentsBase64" + contentsBase64);
			byte[] bytes = Base64.decodeBase64(contentsBase64.toString());
			boolean encrypted = "ENCRYPTED PRIVATE".equals(type);
			if (!encrypted && !"PRIVATE".equals(type)) {
				System.out
						.println("文件起始应该是\"-----BEGIN ENCRYPTED PRIVATE KEY-----\"或者是\"-----BEGIN PRIVATE KEY-----\"");
				return null;
			}
			return getPrivateKeyFromBytes(bytes, encrypted, password);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static PrivateKey getPrivateKeyFromBytes(byte[] bytes, boolean encrypted, String password) throws Exception {
		KeySpec keySpec;
		if (encrypted) {
			if (password == null) {
				throw new Exception("需要加密私钥的密码");
			}
			try {
				EncryptedPrivateKeyInfo encPrvKeyInfo = new EncryptedPrivateKeyInfo(bytes);
				Cipher cipher = Cipher.getInstance(encPrvKeyInfo.getAlgName());
				SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encPrvKeyInfo.getAlgName());
				PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
				Key pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);
				int opMode = Cipher.DECRYPT_MODE;
				AlgorithmParameters params = encPrvKeyInfo.getAlgParameters();
				cipher.init(opMode, pbeKey, params);
				keySpec = encPrvKeyInfo.getKeySpec(cipher);
			} catch (Exception e) {
				throw new Exception("该密码不能解密", e);
			}
		} else {
			keySpec = new PKCS8EncodedKeySpec(bytes);
		}
		try {
			KeyFactory kf;
			try {
				kf = KeyFactory.getInstance("RSA");
				return kf.generatePrivate(keySpec);
			} catch (InvalidKeySpecException e) {
				kf = KeyFactory.getInstance("DSA");
				return kf.generatePrivate(keySpec);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("不支持该算法", e);
		} catch (InvalidKeySpecException e) {
			try {
				return SecureUtil.generatePrivateKey("SM2", bytes);
			} catch (Exception ex) {
				throw new Exception("DSA和RSA、SM2都无法解析", e);
			}
		}
	}

	public static String getSigAlgFromSignKeyType(byte[] hashAlg, String sigKeyType) throws IdentifierException {
		if (Util.equalsBytes(hashAlg, Common.HASH_ALG_SHA1)
				|| Util.equalsBytes(hashAlg, Common.HASH_ALG_SHA1_ALTERNATE))
			return "SHA1with" + sigKeyType;
		else if (Util.equalsBytes(hashAlg, Common.HASH_ALG_SHA256)
				|| Util.equalsBytes(hashAlg, Common.HASH_ALG_SHA256_ALTERNATE))
			return "SHA256with" + sigKeyType;
		else if (Util.equalsBytes(hashAlg, Common.HASH_ALG_MD5))
			return "MD5with" + sigKeyType;
		else if (hashAlg.length == 1 && hashAlg[0] == Common.HASH_CODE_SHA1)
			return "SHA1with" + sigKeyType;
		else if (hashAlg.length == 1 && hashAlg[0] == Common.HASH_CODE_SHA256)
			return "SHA256with" + sigKeyType;
		else if (hashAlg.length == 1 && hashAlg[0] == Common.HASH_CODE_MD5)
			return "MD5with" + sigKeyType;
		throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE,
				"未知的哈希算法: " + Util.decodeString(hashAlg));
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static boolean isIPV4(String ip) {
		if (ip == null)
			return false;
		if (Common.IPV4_REGEX.matcher(ip).matches())
			return true;
		else
			return false;
	}

	public static final byte[] getBytesFromFile(String filePath) throws IdentifierException {
		File f = new File(filePath);
		byte[] buf = new byte[(int) f.length()];

		FileInputStream input = null;
		try {
			input = new FileInputStream(f);
			int n = 0;
			int offset = 0;
			while ((n < buf.length) && ((offset = input.read(buf, n, buf.length - n)) >= 0))
				n += offset;
		} catch (IOException e) {
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, e.getMessage());
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					throw new IdentifierException(ExceptionCommon.SOURCE_IO_ERROR, e.getMessage());
				}	
			}
		}
		return buf;
	}

	public static final byte[] convertIPStr2Bytes(String ip) throws IdentifierException {
		byte[] ipBytes;
		if (Util.isIPV4(ip)) {
			String[] st = ip.split("\\.");
			ipBytes = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) Integer.parseInt(st[0]),
					(byte) Integer.parseInt(st[1]), (byte) Integer.parseInt(st[2]), (byte) Integer.parseInt(st[3]) };
		} else {
			InetAddress addr;
			try {
				addr = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				throw new IdentifierException(ExceptionCommon.UNKNOWN_HOSTNAME_ERROR);
			}
			byte addr1[] = addr.getAddress();
			ipBytes = new byte[Common.IP_ADDRESS_SIZE_SIXTEEN];
			for (int i = 0; i < Common.IP_ADDRESS_SIZE_SIXTEEN; i++)
				ipBytes[i] = (byte) 0;
			System.arraycopy(addr1, 0, ipBytes, ipBytes.length - addr1.length, addr1.length);
		}
		return ipBytes;
	}

	public static byte[] reverseOrderArray(byte[] arr) {
		// 定义一个反序后的数组
		byte[] desArr = new byte[arr.length];
		// 把原数组元素倒序遍历
		for (int i = 0; i < arr.length; i++) {
			// 把arr的第i个元素赋值给desArr的最后第i个元素中
			desArr[arr.length - 1 - i] = arr[i];
		}
		// 返回倒序后的数组
		return desArr;
	}

	public static byte[] toBytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}

		return bytes;
	}

	public static byte[] getBytesWithout00(byte[] origin) {
		byte[] result = origin;
		if (origin[0] == 0) {
			result = new byte[origin.length - 1];
			System.arraycopy(origin, 1, result, 0, result.length);
		}
		return result;
	}

	/**
	 * 字节数组拼接
	 * 
	 * @param params
	 * @return
	 */
	public static byte[] join(byte[]... params) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] res = null;
		try {
			for (int i = 0; i < params.length; i++) {
				baos.write(params[i]);
			}
			res = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
