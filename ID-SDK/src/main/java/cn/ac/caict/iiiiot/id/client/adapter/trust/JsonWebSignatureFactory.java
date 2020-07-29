package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PrivateKey;

public abstract class JsonWebSignatureFactory {
    abstract public JsonWebSignature create(String payload, PrivateKey privateKey) throws TrustException;

    abstract public JsonWebSignature create(byte[] payload, PrivateKey privateKey) throws TrustException;

    abstract public JsonWebSignature deserialize(String serialization) throws TrustException;

    private static JsonWebSignatureFactory INSTANCE = new JsonWebSignatureFactoryImpl();

    public static JsonWebSignatureFactory getInstance() {
        return INSTANCE;
    }
}