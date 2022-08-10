package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PublicKey;

public interface JWS {
    public String getPayloadAsString();

    public byte[] getPayloadAsBytes();

    public boolean validates(PublicKey publicKey) throws IdentifierTrustException;

    public String serialize();

    public String serializeToJson();
}