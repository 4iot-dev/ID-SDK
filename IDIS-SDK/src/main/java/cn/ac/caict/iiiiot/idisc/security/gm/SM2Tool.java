package cn.ac.caict.iiiiot.idisc.security.gm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import cn.ac.caict.iiiiot.idisc.utils.Util;
import cn.hutool.crypto.SecureUtil;

import org.apache.commons.codec.binary.Base64;

/**
 * SM2公钥加密算法实现 包括 -签名,验签 -密钥交换 -公钥加密,私钥解密
 */
public class SM2Tool {
	public static BigInteger n = new BigInteger(
			"FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
	private static BigInteger p = new BigInteger(
			"FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
	private static BigInteger a = new BigInteger(
			"FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
	private static BigInteger b = new BigInteger(
			"28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
	private static BigInteger gx = new BigInteger(
			"32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
	private static BigInteger gy = new BigInteger(
			"BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);
	public static ECDomainParameters ecc_bc_spec;
	private static int w = (int) Math.ceil(n.bitLength() * 1.0 / 2) - 1;
	private static BigInteger _2w = new BigInteger("2").pow(w);
	private static final int DIGEST_LENGTH = 32;

	private static SecureRandom random = new SecureRandom();
	public static ECCurve.Fp curve;
	public static ECPoint G;
	private boolean debug = false;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 以16进制打印字节数组
	 * 
	 * @param b
	 */
	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase());
		}
		System.out.println();
	}

	/**
	 * 随机数生成器
	 * 
	 * @param max
	 * @return
	 */
	private static BigInteger random(BigInteger max) {

		BigInteger r = new BigInteger(256, random);
		while (r.compareTo(max) >= 0) {
			r = new BigInteger(128, random);
		}
		return r;
	}

	/**
	 * 判断字节数组是否全0
	 * 
	 * @param buffer
	 * @return
	 */
	private boolean allZero(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] != 0)
				return false;
		}
		return true;
	}

	/**
	 * 公钥加密
	 * 
	 * @param input
	 *            加密原文
	 * @param publicKey
	 *            公钥
	 * @return
	 */
	public byte[] encrypt(String input, ECPoint publicKey) {
		byte[] inputBuffer = input.getBytes();
		if (debug)
			printHexString(inputBuffer);

		byte[] C1Buffer;
		ECPoint kpb;
		byte[] t;
		do {
			/* 1 产生随机数k，k属于[1, n-1] */
			BigInteger k = random(n);
			if (debug) {
				System.out.print("k: ");
				printHexString(k.toByteArray());
			}

			/* 2 计算椭圆曲线点C1 = [k]G = (x1, y1) */
			ECPoint C1 = G.multiply(k);
			C1Buffer = C1.getEncoded(false);
			if (debug) {
				System.out.print("C1: ");
				printHexString(C1Buffer);
			}

			/*
			 * 3 计算椭圆曲线点 S = [h]Pb
			 */
			BigInteger h = ecc_bc_spec.getH();
			if (h != null) {
				ECPoint S = publicKey.multiply(h);
				if (S.isInfinity())
					throw new IllegalStateException();
			}

			/* 4 计算 [k]PB = (x2, y2) */
			kpb = publicKey.multiply(k).normalize();

			/* 5 计算 t = KDF(x2||y2, klen) */
			byte[] kpbBytes = kpb.getEncoded(false);
			t = KDF(kpbBytes, inputBuffer.length);
		} while (allZero(t));

		/* 6 计算C2=M^t */
		byte[] C2 = new byte[inputBuffer.length];
		for (int i = 0; i < inputBuffer.length; i++) {
			C2[i] = (byte) (inputBuffer[i] ^ t[i]);
		}

		/* 7 计算C3 = Hash(x2 || M || y2) */
		byte[] C3 = sm3hash(kpb.getXCoord().toBigInteger().toByteArray(), inputBuffer,
				kpb.getYCoord().toBigInteger().toByteArray());

		/* 8 输出密文 C=C1 || C2 || C3 */

		byte[] encryptResult = new byte[C1Buffer.length + C2.length + C3.length];

		System.arraycopy(C1Buffer, 0, encryptResult, 0, C1Buffer.length);
		System.arraycopy(C2, 0, encryptResult, C1Buffer.length, C2.length);
		System.arraycopy(C3, 0, encryptResult, C1Buffer.length + C2.length, C3.length);

		if (debug) {
			System.out.print("密文: ");
			printHexString(encryptResult);
		}

		return encryptResult;
	}

	/**
	 * 私钥解密
	 * 
	 * @param encryptData
	 *            密文数据字节数组
	 * @param privateKey
	 *            解密私钥
	 * @return
	 */
	public String decrypt(byte[] encryptData, BigInteger privateKey) {

		if (debug)
			System.out.println("encryptData length: " + encryptData.length);

		byte[] C1Byte = new byte[65];
		System.arraycopy(encryptData, 0, C1Byte, 0, C1Byte.length);

		ECPoint C1 = curve.decodePoint(C1Byte).normalize();

		/*
		 * 计算椭圆曲线点 S = [h]C1 是否为无穷点
		 */
		BigInteger h = ecc_bc_spec.getH();
		if (h != null) {
			ECPoint S = C1.multiply(h);
			if (S.isInfinity())
				throw new IllegalStateException();
		}
		/* 计算[dB]C1 = (x2, y2) */
		ECPoint dBC1 = C1.multiply(privateKey).normalize();

		/* 计算t = KDF(x2 || y2, klen) */
		byte[] dBC1Bytes = dBC1.getEncoded(false);
		int klen = encryptData.length - 65 - DIGEST_LENGTH;
		byte[] t = KDF(dBC1Bytes, klen);
		if (allZero(t)) {
			System.err.println("all zero");
			throw new IllegalStateException();
		}

		/* 5 计算M'=C2^t */
		byte[] M = new byte[klen];
		for (int i = 0; i < M.length; i++) {
			M[i] = (byte) (encryptData[C1Byte.length + i] ^ t[i]);
		}
		if (debug)
			printHexString(M);

		/* 6 计算 u = Hash(x2 || M' || y2) 判断 u == C3是否成立 */
		byte[] C3 = new byte[DIGEST_LENGTH];

		if (debug)
			try {
				System.out.println("M = " + new String(M, "UTF8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		System.arraycopy(encryptData, encryptData.length - DIGEST_LENGTH, C3, 0, DIGEST_LENGTH);
		byte[] u = sm3hash(dBC1.getXCoord().toBigInteger().toByteArray(), M,
				dBC1.getYCoord().toBigInteger().toByteArray());
		if (Arrays.equals(u, C3)) {
			if (debug)
				System.out.println("解密成功");
			try {
				return new String(M, "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			if (debug) {
				System.out.print("u = ");
				printHexString(u);
				System.out.print("C3 = ");
				printHexString(C3);
				System.err.println("解密验证失败");
			}
			return null;
		}

	}

	/**
	 * 判断是否在范围内
	 * 
	 * @param param
	 * @param min
	 * @param max
	 * @return
	 */
	private boolean between(BigInteger param, BigInteger min, BigInteger max) {
		if (param.compareTo(min) >= 0 && param.compareTo(max) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断生成的公钥是否合法
	 * 
	 * @param publicKey
	 * @return
	 */
	private boolean checkPublicKey(ECPoint publicKey) {

		if (!publicKey.isInfinity()) {

			BigInteger x = publicKey.getXCoord().toBigInteger();
			BigInteger y = publicKey.getYCoord().toBigInteger();

			if (between(x, new BigInteger("0"), p) && between(y, new BigInteger("0"), p)) {

				BigInteger xResult = x.pow(3).add(a.multiply(x)).add(b).mod(p);

				if (debug)
					System.out.println("xResult: " + xResult.toString());

				BigInteger yResult = y.pow(2).mod(p);

				if (debug)
					System.out.println("yResult: " + yResult.toString());

				if (yResult.equals(xResult) && publicKey.multiply(n).isInfinity()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 生成密钥对
	 * 
	 * @return
	 */
	public SM2KeyPair generateKeyPair() {

		BigInteger d = random(n.subtract(new BigInteger("1")));

		SM2KeyPair keyPair = new SM2KeyPair(G.multiply(d).normalize(), d);

		if (checkPublicKey(keyPair.getPublicKey())) {
			if (debug)
				System.out.println("generate key successfully");
			return keyPair;
		} else {
			if (debug)
				System.err.println("generate key failed");
			return null;
		}
	}

	public SM2Tool() {
		curve = new ECCurve.Fp(p, // q
				a, // a
				b); // b
		G = curve.createPoint(gx, gy);
		ecc_bc_spec = new ECDomainParameters(curve, G, n);
	}

	public SM2Tool(boolean debug) {
		this();
		this.debug = debug;
	}

	/**
	 * 导出公钥到本地
	 * 
	 * @param publicKey
	 * @param path
	 */
	public void exportPublicKey(ECPoint publicKey, String path) {
		File file = new File(path);
		try {
			if (!file.exists())
				file.createNewFile();
			byte buffer[] = publicKey.getEncoded(false);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从本地导入公钥
	 * @param path
	 * @return
	 */
	public ECPoint importPublicKey(String path) {
		File file = new File(path);
		try {
			if (!file.exists())
				return null;
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte buffer[] = new byte[16];
			int size;
			while ((size = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, size);
			}
			fis.close();
			byte[] decode = readPemFile(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
			PublicKey pub = SecureUtil.generatePublicKey("SM2", decode);
			System.out.println(pub.getClass());
			ECPoint point = ((BCECPublicKey)pub).getQ();
			byte[] qBytes = point.getEncoded(false);
			System.out.println("[importpubkey]test_point:" + Util.bytesToHexString(qBytes));
			return curve.decodePoint(qBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PublicKey loadPublicKey(String path){
		File file = new File(path);
		try {
			if (!file.exists())
				return null;
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte buffer[] = new byte[16];
			int size;
			while ((size = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, size);
			}
			fis.close();
			byte[] decode = readPemFile(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
			return SecureUtil.generatePublicKey("SM2", decode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Pattern head_TailPattern = Pattern.compile("^\\s*-----BEGIN (.*) KEY-----\\s*$");

	private static byte[] readPemFile(Reader reader) {
		BufferedReader bufferedReader;
		if (reader instanceof BufferedReader)
			bufferedReader = (BufferedReader) reader;
		else
			bufferedReader = new BufferedReader(reader);
		String line;
		StringBuilder base64Only = new StringBuilder();
		String keyType = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				if (keyType == null) {
					Matcher m = head_TailPattern.matcher(line);
					if (m.matches())
						keyType = m.group(1);
					else
						keyType = "";
				}
				if (line.startsWith("-----"))
					continue;
				base64Only.append(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		byte[] bytes = Base64.decodeBase64(base64Only.toString());
		return bytes;
	}

	/**
	 * 导出私钥到本地
	 * 
	 * @param privateKey
	 * @param path
	 */
	public void exportPrivateKey(BigInteger privateKey, String path) {
		File file = new File(path);
		try {
			if (!file.exists())
				file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(privateKey);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从本地导入私钥
	 * 
	 * @param path
	 * @return
	 */
	public BigInteger importPrivateKey(String path) {
		File file = new File(path);
		try {
			if (!file.exists())
				return null;
			byte[] decode = readPemFile(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
			byte[] dest = new byte[32];
			System.arraycopy(decode, 36, dest, 0, 32);
			System.out.println(Util.bytesToHexString(dest));
			PrivateKey key = SecureUtil.generatePrivateKey("SM2", decode);
			System.out.println("[importPrivateKey]alg:" + key.getAlgorithm());
			System.out.println("privatekey:" + ((BCECPrivateKey) key).getD());
			BigInteger b = ((BCECPrivateKey) key).getD();
			ECPoint g2 = ((BCECPrivateKey) key).getParameters().getG();
			System.out.println("[importPrivateKey]x:" + Util.bytesToHexString(g2.getXCoord().getEncoded()));
			System.out.println("[importPrivateKey]y:" + Util.bytesToHexString(g2.getYCoord().getEncoded()));
			System.out.println(Util.bytesToHexString(b.toByteArray()));
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PrivateKey loadPrivateKey(String path){
		File file = new File(path);
		try {
			if (!file.exists())
				return null;
			byte[] decode = readPemFile(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
			return SecureUtil.generatePrivateKey("SM2", decode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * sm3摘要
	 * @param params
	 * @return
	 */
	private static byte[] sm3hash(byte[]... params) {
		byte[] res = null;
		try {
			res = SM3Tool.hash(Util.join(params));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 取得用户标识字节数组
	 * @param IDA
	 * @param aPublicKey
	 * @return
	 */
	private static byte[] ZA(String IDA, ECPoint aPublicKey) {
		byte[] idaBytes = IDA.getBytes();
		int entlenA = idaBytes.length * 8;
		byte[] ENTLA = new byte[] { (byte) (entlenA & 0xFF00), (byte) (entlenA & 0x00FF) };
		byte[] aa = a.toByteArray();
		byte[] aa_ = Util.getBytesWithout00(aa);
		byte[] bb = b.toByteArray();
		byte[] bb_ = Util.getBytesWithout00(bb);
		byte[] ggxx = gx.toByteArray();
		byte[] ggxx_ = Util.getBytesWithout00(ggxx);
		byte[] ggyy = gy.toByteArray();
		byte[] ggyy_ = Util.getBytesWithout00(ggyy);
		byte[] x_coord = aPublicKey.getXCoord().toBigInteger().toByteArray();
		byte[] x_coord_ = Util.getBytesWithout00(x_coord);
		byte[] y_coord = aPublicKey.getYCoord().toBigInteger().toByteArray();
		byte[] y_coord_ = Util.getBytesWithout00(y_coord);

		byte[] ZA = sm3hash(ENTLA, idaBytes, aa_, bb_, ggxx_, ggyy_, x_coord_, y_coord_);
		
		System.out.println("ENTLA:" + Util.bytesToHexString(ENTLA));
		System.out.println("idaBytes:" + Util.bytesToHexString(idaBytes));
		System.out.println("a:" + Util.bytesToHexString(aa_));
		System.out.println("b:" + Util.bytesToHexString(bb_));
		System.out.println("gx:" + Util.bytesToHexString(ggxx_));
		System.out.println("gy:" + Util.bytesToHexString(ggyy_));
		System.out.println("xCoord:" + Util.bytesToHexString(x_coord_));
		System.out.println("yCoord:" + Util.bytesToHexString(y_coord_));
		System.out.println("ZA:" + Util.bytesToHexString(ZA));
		return ZA;
	}

	/**
	 * 签名
	 * 
	 * @param M
	 *            签名信息
	 * @param IDA
	 *            签名方唯一标识
	 * @param keyPair
	 *            签名方密钥对
	 * @return 签名
	 */
	public Signature sign(String M, String IDA, SM2KeyPair keyPair) {
		byte[] ZA = ZA(IDA, keyPair.getPublicKey());
		byte[] M_ = Util.join(ZA, M.getBytes());
		System.out.println("[sign]M_" + Util.bytesToHexString(M_));
		BigInteger e = new BigInteger(1, sm3hash(M_));
		byte[] digests = sm3hash(M_);
		System.out.println("[sign]最终摘要：" + Util.bytesToHexString(digests));
		BigInteger k;
		BigInteger r;
		do {
			k = random(n);
			ECPoint p1 = G.multiply(k).normalize();
			BigInteger x1 = p1.getXCoord().toBigInteger();
			r = e.add(x1);
			r = r.mod(n);
		} while (r.equals(BigInteger.ZERO) || r.add(k).equals(n));

		BigInteger s = ((keyPair.getPrivateKey().add(BigInteger.ONE).modInverse(n))
				.multiply((k.subtract(r.multiply(keyPair.getPrivateKey()))).mod(n))).mod(n);

		return new Signature(r, s);
	}

	/**
	 * 验签
	 * 
	 * @param M
	 *            签名信息
	 * @param signature
	 *            签名
	 * @param IDA
	 *            签名方唯一标识
	 * @param aPublicKey
	 *            签名方公钥
	 * @return true or false
	 */
	public boolean verify(String M, Signature signature, String IDA, ECPoint aPublicKey) {
		if (!between(signature.r, BigInteger.ONE, n))
			return false;
		if (!between(signature.s, BigInteger.ONE, n))
			return false;
		byte[] M_ = Util.join(ZA(IDA, aPublicKey), M.getBytes());
		System.out.println("[verify]对---" + Util.bytesToHexString(M_) + "---做摘要");
		BigInteger e = new BigInteger(1, sm3hash(M_));
		System.out.println("[verify]e:" + e);
		System.out.println("[verify]最终摘要：" + Util.bytesToHexString(sm3hash(M_)) + " 摘要长度：" + sm3hash(M_).length);
		BigInteger t = signature.r.add(signature.s).mod(n);
		System.out.println("[verify]t:" + t);
		if (t.equals(BigInteger.ZERO))
			return false;

		ECPoint p1 = G.multiply(signature.s).normalize();
		System.out.println("[verify]p1:" + p1.toString());
		ECPoint p2 = aPublicKey.multiply(t).normalize();
		System.out.println("[verify]p2:" + p2.toString());
		BigInteger x1 = p1.add(p2).normalize().getXCoord().toBigInteger();
		System.out.println("[verify]x1:" + x1);
		BigInteger R = e.add(x1).mod(n);
		System.out.println("[verify]R:" + R);
		if (R.equals(signature.r))
			return true;
		return false;
	}

	/**
	 * 密钥派生函数
	 * 
	 * @param Z
	 * @param klen
	 *            生成klen字节数长度的密钥
	 * @return
	 */
	private static byte[] KDF(byte[] Z, int klen) {
		int ct = 1;
		int end = (int) Math.ceil(klen * 1.0 / 32);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			for (int i = 1; i < end; i++) {
				baos.write(sm3hash(Z, SM3Tool.toByteArray(ct)));
				ct++;
			}
			byte[] last = sm3hash(Z, SM3Tool.toByteArray(ct));
			if (klen % 32 == 0) {
				baos.write(last);
			} else
				baos.write(last, 0, klen % 32);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		byte[] digest = sm3hash("hello".getBytes());
		System.out.println(Util.bytesToHexString(digest));
	}

	public static class Signature {
		public BigInteger r;
		public BigInteger s;

		public Signature(BigInteger r, BigInteger s) {
			this.r = r;
			this.s = s;
		}

		public String toString() {
			return r.toString(16) + "," + s.toString(16);
		}
	}
}
