package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import cn.ac.caict.iiiiot.id.client.utils.Util;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonParser;


public class JWSImpl implements JWS {
    private final String hashAlg;
    private final String keyAlg;
    private final byte[] header;
    private final byte[] serializedHeader;
    private final byte[] payload;
    private final byte[] serializedPayload;
    private final byte[] signature;
    private final byte[] serializedSignature;

    public JWSImpl(String payload, PrivateKey privateKey) throws IdentifierTrustException {
        this(Util.encodeString(payload), privateKey);
    }

    public JWSImpl(byte[] payload, PrivateKey privateKey) throws IdentifierTrustException {
        this.payload = payload;
        keyAlg = privateKey.getAlgorithm();
        if ("RSA".equals(keyAlg)) {
            hashAlg = "SHA256";
            header = Util.encodeString("{\"alg\":\"RS256\"}");
        } else if ("DSA".equals(keyAlg)) {
            hashAlg = "SHA256";
            header = Util.encodeString("{\"alg\":\"DS256\"}");
        } else {
            throw new IllegalArgumentException("Unsupported key algorithm " + keyAlg);
        }
        serializedHeader = Base64.encodeBase64URLSafe(header);
        serializedPayload = Base64.encodeBase64URLSafe(payload);
        try {
            Signature sig = Signature.getInstance(hashAlg + "with" + keyAlg);
            sig.initSign(privateKey);
            sig.update(serializedHeader);
            sig.update((byte)'.');
            sig.update(serializedPayload);
            signature = sig.sign();
            serializedSignature = Base64.encodeBase64URLSafe(signature);
        } catch (Exception e) {
            throw new IdentifierTrustException("Error creating JWS", e);
        }
    }

    public JWSImpl(String serialization) throws IdentifierTrustException {
        if (isCompact(serialization)) {
            try {
                String[] dotSeparatedParts = serialization.split("\\.");
                serializedHeader = Util.encodeString(dotSeparatedParts[0]);
                header = Base64.decodeBase64(serializedHeader);
                serializedPayload = Util.encodeString(dotSeparatedParts[1]);
                payload = Base64.decodeBase64(serializedPayload);
                serializedSignature = Util.encodeString(dotSeparatedParts[2]);
                signature = Base64.decodeBase64(serializedSignature);
            } catch (Exception e) {
                throw new IdentifierTrustException("Couldn't parse JWS", e);
            }
        } else {
            Gson gson = GsonCompose.getGson();
            JWSJsonSerialization jwsjs = gson.fromJson(serialization, JWSJsonSerialization.class);
            serializedHeader = Util.encodeString(jwsjs.signatures.get(0).protectedPart);
            header = Base64.decodeBase64(serializedHeader);
            serializedPayload = Util.encodeString(jwsjs.payload);
            payload = Base64.decodeBase64(serializedPayload);
            serializedSignature = Util.encodeString(jwsjs.signatures.get(0).signature);
            signature = Base64.decodeBase64(serializedSignature);
        }
        String algString = getAlgStringFromHeader(header);
        keyAlg = getKeyAlgFromAlg(algString);
        hashAlg = getHashAlgFromAlg(algString);
    }

    private static String getAlgStringFromHeader(byte[] header) throws IdentifierTrustException {
        try {
            System.out.println(Util.bytesToHexString(header));
            String alg = new JsonParser().parse(Util.decodeString(header))
                .getAsJsonObject()
                .get("alg")
                .getAsString();
            return alg;
        } catch (Exception e) {
            throw new IdentifierTrustException("Couldn't parse JWS header", e);
        }
    }

    private static String getKeyAlgFromAlg(String alg) throws IdentifierTrustException {
        if (alg.startsWith("RS")) return "RSA";
        else if (alg.startsWith("DS")) return "DSA";
        throw new IdentifierTrustException("Couldn't parse JWS header");
    }

    private static String getHashAlgFromAlg(String alg) throws IdentifierTrustException {
        if (alg.endsWith("256")) return "SHA256";
        else if (alg.endsWith("160") || alg.endsWith("128") || alg.equals("DSA") || alg.equals("DS")) return "SHA1";
        else if (alg.endsWith("384")) return "SHA384";
        else if (alg.endsWith("512")) return "SHA512";
        throw new IdentifierTrustException("Couldn't parse JWS header");
    }

    private static boolean isCompact(String serialization) {
        return !serialization.trim().startsWith("{");
    }

    @Override
    public String getPayloadAsString() {
        return Util.decodeString(payload);
    }

    @Override
    public byte[] getPayloadAsBytes() {
        return payload.clone();
    }

    @Override
    public boolean validates(PublicKey publicKey) throws IdentifierTrustException {
        if (!keyAlg.equals(publicKey.getAlgorithm())) return false;
        try {
            Signature sig = Signature.getInstance(hashAlg + "with" + publicKey.getAlgorithm());
            sig.initVerify(publicKey);
            sig.update(serializedHeader);
            sig.update((byte)'.');
            sig.update(serializedPayload);
            return sig.verify(signature);
        } catch (Exception e) {
            throw new IdentifierTrustException("Error validating JWS", e);
        }
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(Util.decodeString(serializedHeader));
        sb.append('.');
        sb.append(Util.decodeString(serializedPayload));
        sb.append('.');
        sb.append(Util.decodeString(serializedSignature));
        return sb.toString();
    }

    @Override
    public String serializeToJson() {
        String headerEncoded = Util.decodeString(serializedHeader);
        String payloadEncoded = Util.decodeString(serializedPayload);
        String signatureEncoded = Util.decodeString(serializedSignature);
        String json = "{\"payload\":\"" + payloadEncoded + "\",\"signatures\":[{\"protected\":\"" + headerEncoded + "\",\"signature\":\"" + signatureEncoded + "\"}]}";
        return json;
    }

}