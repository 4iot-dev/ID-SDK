package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.List;

public class ChainVerificationReport {
    public ValuesSignatureVerificationReport valuesReport;
    public List<IssuedSignatureVerificationReport> issuedSignatureVerificationReports;
    public boolean rootIsTrusted;
    public boolean chainNeedsRequiredSigner;
    public boolean chainGoodUpToRequiredSigner;
    public boolean unableToBuildChain;

    public boolean canTrustAndAuthorized() {
        if (!rootIsTrusted) return false;
        if (!valuesReport.correctHandle) return false;
        if (!valuesReport.canTrust()) return false;
        for (IssuedSignatureVerificationReport issuedSignatureVerificationReport : issuedSignatureVerificationReports) {
            if (!issuedSignatureVerificationReport.canTrustAndAuthorized()) return false;
        }
        return true;
    }

    public boolean canTrustAndAuthorizedUpToRequiredSigner() {
        if (!chainNeedsRequiredSigner) return false; // not relevant in this case
        if (!rootIsTrusted) return false;
        if (isRequiredSignerNeededAndChainIsGoodUpToRequiredSigner()) return false;
        if (!valuesReport.correctHandle) return false;
        if (!valuesReport.canTrust()) return false;
        // no need to check issued sigs, since ChainVerifier checks them when setting chainGoodUpToLocalCert
        return true;
    }

    public boolean isRequiredSignerNeededAndChainIsGoodUpToRequiredSigner() {
        return (chainNeedsRequiredSigner && !chainGoodUpToRequiredSigner);
    }

    public boolean canTrust() {
        if (!rootIsTrusted) return false;
        for (IssuedSignatureVerificationReport issuedSignatureVerificationReport : issuedSignatureVerificationReports) {
            if (!issuedSignatureVerificationReport.canTrust()) return false;
        }
        return true;
    }
}