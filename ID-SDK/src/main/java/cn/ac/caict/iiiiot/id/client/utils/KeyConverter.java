package cn.ac.caict.iiiiot.id.client.utils;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KeyConverter {
    private static class BytesAndKeyType {
        byte[] bytes;
        String keyType;

        public BytesAndKeyType(byte[] bytes, String keyType) {
            this.bytes = bytes;
            this.keyType = keyType;
        }
    }

    private static Pattern firstLinePattern = Pattern.compile("^\\s*-----BEGIN (.*) KEY-----\\s*$");

    private static BytesAndKeyType readPemFile(Reader reader) {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) bufferedReader = (BufferedReader) reader;
        else bufferedReader = new BufferedReader(reader);
        String line;
        StringBuilder base64Only = new StringBuilder();
        String keyType = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (keyType == null) {
                    Matcher m = firstLinePattern.matcher(line);
                    if (m.matches()) keyType = m.group(1);
                    else keyType = "";
                }
                if (line.startsWith("-----")) continue;
                base64Only.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        byte[] bytes = Base64.getDecoder().decode(base64Only.toString());
        return new BytesAndKeyType(bytes, keyType);
    }

    public static String toX509Pem(PublicKey publicKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\r\n");
        byte[] data = Base64.getMimeEncoder().encode(publicKey.getEncoded());
        for (byte b : data) {
            sb.append((char) b);
        }
        if (data[data.length - 1] != '\n') sb.append("\r\n");
        sb.append("-----END PUBLIC KEY-----\r\n");
        return sb.toString();
    }

    public static PublicKey publicKeyFromBytes(byte[] bytes) throws Exception {
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
            throw new Exception("Neither RSA nor DSA public key generator can parse", e);
        }
    }

    public static PublicKey fromX509Pem(String pem) throws Exception {
        BytesAndKeyType bytesAndKeyType = readPemFile(new StringReader(pem));
        if (!"PUBLIC".equals(bytesAndKeyType.keyType)) {
            throw new Exception("Expected -----BEGIN PUBLIC KEY-----");
        }
        return publicKeyFromBytes(bytesAndKeyType.bytes);
    }

    public static String toPkcs8UnencryptedPem(PrivateKey privateKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PRIVATE KEY-----\r\n");
        byte[] data = Base64.getMimeEncoder().encode(privateKey.getEncoded());
        for (byte b : data) {
            sb.append((char) b);
        }
        if (data[data.length - 1] != '\n') sb.append("\r\n");
        sb.append("-----END PRIVATE KEY-----\r\n");
        return sb.toString();
    }

    public static String toPkcs8EncryptedPem(PrivateKey privateKey, String passphrase) {
        String alg = "PBEWithSHA1AndDESede";
        int count = 10000;// hash iteration count
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] encryptedPkcs8;
        try {
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(alg);
            SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

            Cipher pbeCipher = Cipher.getInstance(alg);
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            byte[] ciphertext = pbeCipher.doFinal(privateKey.getEncoded());

            // Now construct  PKCS #8 EncryptedPrivateKeyInfo object
            AlgorithmParameters algparms = AlgorithmParameters.getInstance(alg);
            algparms.init(pbeParamSpec);
            EncryptedPrivateKeyInfo encinfo = new EncryptedPrivateKeyInfo(algparms, ciphertext);
            // and here we have it! a DER encoded PKCS#8 encrypted key!
            encryptedPkcs8 = encinfo.getEncoded();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ENCRYPTED PRIVATE KEY-----\r\n");
        byte[] data = Base64.getMimeEncoder().encode(encryptedPkcs8);
        for (byte b : data) {
            sb.append((char) b);
        }
        if (data[data.length - 1] != '\n') sb.append("\r\n");
        sb.append("-----END ENCRYPTED PRIVATE KEY-----\r\n");
        return sb.toString();
    }

    public static PrivateKey privateKeyFromBytes(byte[] bytes, boolean encrypted, String passphrase) throws Exception {
        KeySpec keySpec;
        if (encrypted) {
            if (passphrase == null) {
                throw new Exception("Encrypted key, passphrase required");
            }
            try {
                keySpec = keySpecFromEncryptedBytes(bytes, passphrase);
            } catch (Exception e) {
                throw new Exception("Unable to decrypt private key", e);
            }
        } else {
            keySpec = new PKCS8EncodedKeySpec(bytes);
        }
        try {
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                return KeyFactory.getInstance("DSA").generatePrivate(keySpec);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("Neither RSA nor DSA private key generator can parse", e);
        }
    }

    public static PrivateKey fromPkcs8Pem(String pem, String passphrase) throws Exception {
        BytesAndKeyType bytesAndKeyType = readPemFile(new StringReader(pem));
        boolean encrypted = "ENCRYPTED PRIVATE".equals(bytesAndKeyType.keyType);
        if (!encrypted && !"PRIVATE".equals(bytesAndKeyType.keyType)) {
            throw new Exception("Expected -----BEGIN [ENCRYPTED] PRIVATE KEY-----");
        }
        return privateKeyFromBytes(bytesAndKeyType.bytes, encrypted, passphrase);
    }

    private static KeySpec keySpecFromEncryptedBytes(byte[] bytes, String passphrase) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeySpec keySpec;
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(bytes);
        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
        Key pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
        keySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
        return keySpec;
    }

}