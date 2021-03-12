package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JWSJsonSerialization {
    String payload;
    List<JsonWebSignatureSignatureJsonSerialization> signatures;

    public static class JsonWebSignatureSignatureJsonSerialization {
        @SerializedName("protected")
        String protectedPart;
        String signature;
    }
}