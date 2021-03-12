package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PrivateKey;

public class JWSFactoryImpl extends JWSFactory {

    @Override
    public JWS create(String payload, PrivateKey privateKey) throws IdentifierTrustException {
        JWS jws = new JWSImpl(payload, privateKey);
        return jws;
    }

    @Override
    public JWS create(byte[] payload, PrivateKey privateKey) throws IdentifierTrustException {
        JWS jws = new JWSImpl(payload, privateKey);
        return jws;
    }

    @Override
    public JWS deserialize(String serialization) throws IdentifierTrustException {
        JWS jws = new JWSImpl(serialization);
        return jws;
    }

}