package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PrivateKey;

public class JsonWebSignatureFactoryImpl extends JsonWebSignatureFactory {

    @Override
    public JsonWebSignature create(String payload, PrivateKey privateKey) throws TrustException {
        JsonWebSignature jws = new JsonWebSignatureImpl(payload, privateKey);
        return jws;
    }

    @Override
    public JsonWebSignature create(byte[] payload, PrivateKey privateKey) throws TrustException {
        JsonWebSignature jws = new JsonWebSignatureImpl(payload, privateKey);
        return jws;
    }

    @Override
    public JsonWebSignature deserialize(String serialization) throws TrustException {
        JsonWebSignature jws = new JsonWebSignatureImpl(serialization);
        return jws;
    }

}