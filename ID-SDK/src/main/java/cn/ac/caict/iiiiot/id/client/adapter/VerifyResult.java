package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.adapter.trust.CertChainVerificationResult;

public class VerifyResult {
    private int code;
    private String message;
    private CertChainVerificationResult result;

    public VerifyResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public VerifyResult(int code, String message, CertChainVerificationResult result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
}
