package cn.ac.caict.iiiiot.idisc.utils;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cn.ac.caict.iiiiot.idisc.security.EC_Custom_PublicKey;
import cn.ac.caict.iiiiot.idisc.security.Permission;

public class JsonWorker {

	public static Gson getGson() {
		return initBuilder(new GsonBuilder()).create();
	}

	public static GsonBuilder initBuilder(GsonBuilder gb){
		gb.registerTypeHierarchyAdapter(PublicKey.class,new PublicKeyTypeAdapter());
		gb.registerTypeHierarchyAdapter(Permission.class, new PermissionTypeAdapter());
		return gb;
	}

	
	public static class PermissionTypeAdapter implements JsonSerializer<Permission>,JsonDeserializer<Permission>{

		@Override
		public JsonElement serialize(Permission src, Type typeOfSrc, JsonSerializationContext context) {
			// TODO Auto-generated method stub
			JsonObject jsonObj = new JsonObject();
			if(src.identifier != null || "".equals(src.identifier))
				jsonObj.addProperty("handle",src.identifier);
			jsonObj.addProperty("perm", src.perm);
			return jsonObj;
		}
		
		@Override
		public Permission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			 JsonObject obj = json.getAsJsonObject();
			 String identifier = null;
			 if(obj.get("handle") != null)
				 identifier = obj.get("handle").getAsString();
			 String perm = null;
			 if(obj.get("perm") != null)
				 perm = obj.get("perm").getAsString();
			return new Permission(identifier,perm);
		}

	}
	
	public static class PublicKeyTypeAdapter implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
        public JsonElement serialize(PublicKey key, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            if (key instanceof DSAPublicKey) {
                DSAPublicKey dsaKey = (DSAPublicKey)key;
                byte[] y = dsaKey.getY().toByteArray();
                DSAParams dsaParams = dsaKey.getParams();
                byte[] p = dsaParams.getP().toByteArray();
                byte[] q = dsaParams.getQ().toByteArray();
                byte[] g = dsaParams.getG().toByteArray();
                json.addProperty("kty", "DSA");
                json.addProperty("y", Base64.encodeBase64URLSafeString(y));
                json.addProperty("p", Base64.encodeBase64URLSafeString(p));
                json.addProperty("q", Base64.encodeBase64URLSafeString(q));
                json.addProperty("g", Base64.encodeBase64URLSafeString(g));
            } else if (key instanceof RSAPublicKey) {
                RSAPublicKey rsaKey = (RSAPublicKey)key;
                byte[] n = rsaKey.getModulus().toByteArray();
                byte[] e = rsaKey.getPublicExponent().toByteArray();
                json.addProperty("kty", "RSA");
                json.addProperty("n", Base64.encodeBase64URLSafeString(n));
                json.addProperty("e", Base64.encodeBase64URLSafeString(e));
            } else if(key instanceof BCECPublicKey){
            	BCECPublicKey ecKey = (BCECPublicKey)key;
            	byte[] x = ecKey.getQ().getXCoord().getEncoded();
            	byte[] y = ecKey.getQ().getYCoord().getEncoded();
            	EC_Custom_PublicKey pub = new EC_Custom_PublicKey();
            	pub.x = x;
            	pub.y = y;
            	json.addProperty("kty", "SM2");
            	json.addProperty("x", Base64.encodeBase64URLSafeString(x));
            	json.addProperty("y", Base64.encodeBase64URLSafeString(y));
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
                    DSAPublicKeySpec keySpec = new DSAPublicKeySpec(new BigInteger(1, y),
                            new BigInteger(1, p),
                            new BigInteger(1, q),
                            new BigInteger(1, g));
                    KeyFactory dsaKeyFactory = KeyFactory.getInstance("DSA");
                    return dsaKeyFactory.generatePublic(keySpec);
                } else if ("RSA".equalsIgnoreCase(kty)) {
                    byte[] n = Base64.decodeBase64(obj.get("n").getAsString());
                    byte[] e = Base64.decodeBase64(obj.get("e").getAsString());
                    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(1, n),
                            new BigInteger(1, e));
                    KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
                    return rsaKeyFactory.generatePublic(keySpec);
                } else if("SM2".equalsIgnoreCase(kty)){
                	byte[] x = Base64.decodeBase64(obj.get("x").getAsString());
                	byte[] y = Base64.decodeBase64(obj.get("y").getAsString());
                	EC_Custom_PublicKey ecKey = new EC_Custom_PublicKey();
                	ecKey.x = x;
                	ecKey.y = y;
                	return ecKey;
                } else {
                    throw new UnsupportedOperationException("Unsupported key type " + kty);
                }
            } catch(JsonParseException e) {
                throw e;
            } catch(Exception e) {
                throw new JsonParseException(e);
            }
        }
    }
}
