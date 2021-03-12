package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.List;

public class CertChainVerificationResult {
    public ValuesSignatureVerificationResult valuesResult;
    public List<IssuedSignatureVerificationResult> issuedSignatureVerificationResults;
    public boolean rootIsTrusted;
    public boolean chainNeedsRequiredSigner;
    public boolean chainGoodUpToRequiredSigner;
    public boolean unableToBuildChain;

    public boolean canTrustAndAuthorized() {
        if (!rootIsTrusted) return false;
        if (!valuesResult.correctHandle) return false;
        if (!valuesResult.canTrust()) return false;
        for (IssuedSignatureVerificationResult issuedSignatureVerificationResult : issuedSignatureVerificationResults) {
            if (!issuedSignatureVerificationResult.canTrustAndAuthorized()) {
                return false;
            }
        }
        return true;
    }

    public boolean canTrustAndAuthorizedUpToRequiredSigner() {
        if (!chainNeedsRequiredSigner) return false; // not relevant in this case
        if (!rootIsTrusted) return false;
        if (isRequiredSignerNeededAndChainIsGoodUpToRequiredSigner()) return false;
        if (!valuesResult.correctHandle) return false;
        if (!valuesResult.canTrust()) return false;
        // no need to check issued sigs, since ChainVerifier checks them when setting chainGoodUpToLocalCert
        return true;
    }

    public boolean isRequiredSignerNeededAndChainIsGoodUpToRequiredSigner() {
        return (chainNeedsRequiredSigner && !chainGoodUpToRequiredSigner);
    }

    public boolean canTrust() {
        if (!rootIsTrusted) return false;
        for (IssuedSignatureVerificationResult issuedSignatureVerificationReport : issuedSignatureVerificationResults) {
            if (!issuedSignatureVerificationReport.canTrust()) return false;
        }
        return true;
    }
}