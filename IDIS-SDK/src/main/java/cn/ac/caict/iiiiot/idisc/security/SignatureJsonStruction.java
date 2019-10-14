package cn.ac.caict.iiiiot.idisc.security;

import java.util.List;

public class SignatureJsonStruction {
    String payload;
    List<SignatureJsonInfo> signatures;
    
    public static class SignatureJsonInfo {
        String header;
        String signature;
    }
}
