package cn.ac.caict.iiiiot.id.client.adapter.trust;

import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.Map;

public class GsonCompose {
    /**
     * Register Handle.net type adapters on a given GsonBuilder, to enable serialization and deserialization of various Handle.net types.
     *
     * @param gsonBuilder a GsonBuilder
     * @return the passed-in GsonBuilder.
     */
    public static GsonBuilder setup(GsonBuilder gsonBuilder) {

        gsonBuilder.registerTypeHierarchyAdapter(PublicKey.class, new PublicKeyTypeHierarchyAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(PrivateKey.class, new PrivateKeyTypeHierarchyAdapter());
        return gsonBuilder;
    }

    /**
     * Returns a GsonBuilder which can serialize and deserialize various Handle.net types.
     *
     * @return a GsonBuilder which can serialize and deserialize various Handle.net types.
     */
    public static GsonBuilder getNewGsonBuilder() {
        return setup(new GsonBuilder());
    }

    /**
     * Returns a Gson instance which can serialize and deserialize various Handle.net types.  This Gson instance has HTML escaping disabled.
     *
     * @return a Gson instance which can serialize and deserialize various Handle.net types.
     */
    public static Gson getGson() {
        return GsonHolder.gson;
    }

    /**
     * Returns a Gson instance which can serialize and deserialize various Handle.net types.  This Gson instance has HTML escaping disabled and pretty-printing enabled.
     *
     * @return a Gson instance which can serialize and deserialize various Handle.net types.
     */
    public static Gson getPrettyGson() {
        return PrettyGsonHolder.prettyGson;
    }

    private static class GsonHolder {
        static Gson gson;

        static {
            gson = GsonCompose.setup(new GsonBuilder().disableHtmlEscaping()).create();
        }
    }

    private static class PrettyGsonHolder {
        static Gson prettyGson;

        static {
            prettyGson = GsonCompose.setup(new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()).create();
        }
    }

    private static String lowerCaseFirst(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    static JsonObject lowerCaseIfNeeded(JsonObject json) {
        if (json.has("ServerList") || json.has("Address") || json.has("Port")) {
            JsonObject obj = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                obj.add(lowerCaseFirst(entry.getKey()), entry.getValue());
            }
            return obj;
        } else return json;
    }

    private static JsonElement dataOfType(String type, JsonElement value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("format", type);
        obj.add("value", value);
        return obj;
    }

    static JsonElement serializeBinary(byte[] bytes) {
        JsonObject obj = new JsonObject();
        obj.addProperty("format", "base64");
        obj.addProperty("value", Base64.encodeBase64String(bytes));
        return obj;
    }

    static JsonElement serializeString(String s) {
        JsonObject obj = new JsonObject();
        obj.addProperty("format", "string");
        obj.addProperty("value", s);
        return obj;
    }


    public static class PublicKeyTypeHierarchyAdapter implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
        @Override
        public JsonElement serialize(PublicKey key, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            if (key instanceof DSAPublicKey) {
                DSAPublicKey dsaKey = (DSAPublicKey) key;
                byte[] y = dsaKey.getY().toByteArray();
                DSAParams dsaParams = dsaKey.getParams();
                byte[] p = dsaParams.getP().toByteArray();
                byte[] q = dsaParams.getQ().toByteArray();
                byte[] g = dsaParams.getG().toByteArray();
                json.addProperty("kty", "DSA");
                json.addProperty("y", Base64.encodeBase64URLSafeString(unsigned(y)));
                json.addProperty("p", Base64.encodeBase64URLSafeString(unsigned(p)));
                json.addProperty("q", Base64.encodeBase64URLSafeString(unsigned(q)));
                json.addProperty("g", Base64.encodeBase64URLSafeString(unsigned(g)));
            } else if (key instanceof RSAPublicKey) {
                RSAPublicKey rsaKey = (RSAPublicKey) key;
                byte[] n = rsaKey.getModulus().toByteArray();
                byte[] e = rsaKey.getPublicExponent().toByteArray();
                json.addProperty("kty", "RSA");
                json.addProperty("n", Base64.encodeBase64URLSafeString(unsigned(n)));
                json.addProperty("e", Base64.encodeBase64URLSafeString(unsigned(e)));
            } else {
                throw new UnsupportedOperationException("Unsupported key type " + key.getClass().getName());
            }
            return json;
        }

        @Override
        public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject obj = json.getAsJsonObject();
                String kty = obj.get("kty").getAsString();
                if ("DSA".equalsIgnoreCase(kty)) {
                    byte[] y = Base64.decodeBase64(obj.get("y").getAsString());
                    byte[] p = Base64.decodeBase64(obj.get("p").getAsString());
                    byte[] q = Base64.decodeBase64(obj.get("q").getAsString());
                    byte[] g = Base64.decodeBase64(obj.get("g").getAsString());
                    DSAPublicKeySpec keySpec = new DSAPublicKeySpec(new BigInteger(1, y), new BigInteger(1, p), new BigInteger(1, q), new BigInteger(1, g));
                    KeyFactory dsaKeyFactory = KeyFactory.getInstance("DSA");
                    return dsaKeyFactory.generatePublic(keySpec);
                } else if ("RSA".equalsIgnoreCase(kty)) {
                    byte[] n = Base64.decodeBase64(obj.get("n").getAsString());
                    byte[] e = Base64.decodeBase64(obj.get("e").getAsString());
                    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(1, n), new BigInteger(1, e));
                    KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
                    return rsaKeyFactory.generatePublic(keySpec);
                } else {
                    throw new UnsupportedOperationException("Unsupported key type " + kty);
                }
            } catch (JsonParseException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    public static class PrivateKeyTypeHierarchyAdapter implements JsonSerializer<PrivateKey>, JsonDeserializer<PrivateKey> {
        @Override
        public JsonElement serialize(PrivateKey key, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            if (key instanceof DSAPrivateKey) {
                DSAPrivateKey dsaKey = (DSAPrivateKey) key;
                byte[] x = dsaKey.getX().toByteArray();
                DSAParams dsaParams = dsaKey.getParams();
                byte[] p = dsaParams.getP().toByteArray();
                byte[] q = dsaParams.getQ().toByteArray();
                byte[] g = dsaParams.getG().toByteArray();
                json.addProperty("kty", "DSA");
                json.addProperty("x", Base64.encodeBase64URLSafeString(unsigned(x)));
                json.addProperty("p", Base64.encodeBase64URLSafeString(unsigned(p)));
                json.addProperty("q", Base64.encodeBase64URLSafeString(unsigned(q)));
                json.addProperty("g", Base64.encodeBase64URLSafeString(unsigned(g)));
            } else if (key instanceof RSAPrivateKey) {
                RSAPrivateKey rsaKey = (RSAPrivateKey) key;
                byte[] n = rsaKey.getModulus().toByteArray();
                byte[] d = rsaKey.getPrivateExponent().toByteArray();
                json.addProperty("kty", "RSA");
                if (key instanceof RSAPrivateCrtKey) {
                    RSAPrivateCrtKey rsacrtKey = (RSAPrivateCrtKey) rsaKey;
                    byte[] e = rsacrtKey.getPublicExponent().toByteArray();
                    byte[] p = rsacrtKey.getPrimeP().toByteArray();
                    byte[] q = rsacrtKey.getPrimeQ().toByteArray();
                    byte[] dp = rsacrtKey.getPrimeExponentP().toByteArray();
                    byte[] dq = rsacrtKey.getPrimeExponentQ().toByteArray();
                    byte[] qi = rsacrtKey.getCrtCoefficient().toByteArray();
                    json.addProperty("n", Base64.encodeBase64URLSafeString(unsigned(n)));
                    json.addProperty("e", Base64.encodeBase64URLSafeString(unsigned(e)));
                    json.addProperty("d", Base64.encodeBase64URLSafeString(unsigned(d)));
                    json.addProperty("p", Base64.encodeBase64URLSafeString(unsigned(p)));
                    json.addProperty("q", Base64.encodeBase64URLSafeString(unsigned(q)));
                    json.addProperty("dp", Base64.encodeBase64URLSafeString(unsigned(dp)));
                    json.addProperty("dq", Base64.encodeBase64URLSafeString(unsigned(dq)));
                    json.addProperty("qi", Base64.encodeBase64URLSafeString(unsigned(qi)));
                } else {
                    json.addProperty("n", Base64.encodeBase64URLSafeString(unsigned(n)));
                    json.addProperty("d", Base64.encodeBase64URLSafeString(unsigned(d)));
                }
            } else {
                throw new UnsupportedOperationException("Unsupported key type " + key.getClass().getName());
            }
            return json;
        }

        @Override
        public PrivateKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject obj = json.getAsJsonObject();
                String kty = obj.get("kty").getAsString();
                if ("DSA".equalsIgnoreCase(kty)) {
                    byte[] x = Base64.decodeBase64(obj.get("x").getAsString());
                    byte[] p = Base64.decodeBase64(obj.get("p").getAsString());
                    byte[] q = Base64.decodeBase64(obj.get("q").getAsString());
                    byte[] g = Base64.decodeBase64(obj.get("g").getAsString());
                    DSAPrivateKeySpec keySpec = new DSAPrivateKeySpec(new BigInteger(1, x), new BigInteger(1, p), new BigInteger(1, q), new BigInteger(1, g));
                    KeyFactory dsaKeyFactory = KeyFactory.getInstance("DSA");
                    return dsaKeyFactory.generatePrivate(keySpec);
                } else if ("RSA".equalsIgnoreCase(kty)) {
                    byte[] n = Base64.decodeBase64(obj.get("n").getAsString());
                    byte[] d = Base64.decodeBase64(obj.get("d").getAsString());
                    RSAPrivateKeySpec keySpec;
                    if (obj.has("qi")) {
                        byte[] e = Base64.decodeBase64(obj.get("e").getAsString());
                        byte[] p = Base64.decodeBase64(obj.get("p").getAsString());
                        byte[] q = Base64.decodeBase64(obj.get("q").getAsString());
                        byte[] dp = Base64.decodeBase64(obj.get("dp").getAsString());
                        byte[] dq = Base64.decodeBase64(obj.get("dq").getAsString());
                        byte[] qi = Base64.decodeBase64(obj.get("qi").getAsString());
                        keySpec = new RSAPrivateCrtKeySpec(new BigInteger(1, n), new BigInteger(1, e), new BigInteger(1, d), new BigInteger(1, p), new BigInteger(1, q), new BigInteger(1, dp), new BigInteger(1, dq), new BigInteger(1, qi));
                    } else {
                        keySpec = new RSAPrivateKeySpec(new BigInteger(1, n), new BigInteger(1, d));
                    }
                    KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
                    return rsaKeyFactory.generatePrivate(keySpec);
                } else {
                    throw new UnsupportedOperationException("Unsupported key type " + kty);
                }
            } catch (JsonParseException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    private static byte[] unsigned(byte[] arr) {
        if (arr.length == 0) return new byte[1];
        int zeros = 0;
        for (byte element : arr) {
            if (element == 0) zeros++;
            else break;
        }
        if (zeros == arr.length) zeros--;
        if (zeros == 0) return arr;
        byte[] res = new byte[arr.length - zeros];
        System.arraycopy(arr, zeros, res, 0, arr.length - zeros);
        return res;
    }

    private static <T> void ensureNoTrailingComma(T[] arr) {
        if (arr == null || arr.length == 0) return;
        if (arr[arr.length - 1] == null)
            throw new JsonParseException("While parsing JSON found array ending with null");
    }
}