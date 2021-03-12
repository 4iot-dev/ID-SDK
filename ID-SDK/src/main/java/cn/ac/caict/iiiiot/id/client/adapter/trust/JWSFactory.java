package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PrivateKey;

public abstract class JWSFactory {
    abstract public JWS create(String payload, PrivateKey privateKey) throws IdentifierTrustException;

    abstract public JWS create(byte[] payload, PrivateKey privateKey) throws IdentifierTrustException;

    abstract public JWS deserialize(String serialization) throws IdentifierTrustException;

    private static JWSFactory INSTANCE = new JWSFactoryImpl();

    public static JWSFactory getInstance() {
        return INSTANCE;
    }
}