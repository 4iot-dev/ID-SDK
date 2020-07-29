package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.PublicKey;

public interface JsonWebSignature {
    public String getPayloadAsString();

    public byte[] getPayloadAsBytes();

    public boolean validates(PublicKey publicKey) throws TrustException;

    public String serialize();

    public String serializeToJson();
}