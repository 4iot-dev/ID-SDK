package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class CertChainVerifier {
    private static final String TRUST_ROOT_IDENTIFIER = "88";
    private final IdentifierVerifier identifierVerifier = new IdentifierVerifier();

    private final List<PublicKey> rootKeys;
    private RequiredSigners requiredSigners;

    public CertChainVerifier(List<PublicKey> rootKeys) {
        this.rootKeys = rootKeys;
    }

    public CertChainVerifier(List<PublicKey> rootKeys, RequiredSigners requiredSigners) {
        this.rootKeys = rootKeys;
        this.requiredSigners = requiredSigners;
    }

    public CertChainVerificationResult verifyValues(String identifier, List<IdentifierValue> values, List<IssuedSignature> issuedSignatures) {
        CertChainVerificationResult report = new CertChainVerificationResult();
        ValuesSignatureVerificationResult valuesReport = identifierVerifier.verifyValues(identifier, values, issuedSignatures.get(0).jws, issuedSignatures.get(0).issuerPublicKey);
        report.valuesResult = valuesReport;
        setChainReportValues(report, identifier, issuedSignatures);
        return report;
    }

    public CertChainVerificationResult verifyChain(List<IssuedSignature> issuedSignatures) {
        CertChainVerificationResult report = new CertChainVerificationResult();
        setChainReportValues(report, null, issuedSignatures);
        return report;
    }

    private void setChainReportValues(CertChainVerificationResult report, String identifier, List<IssuedSignature> issuedSignatures) {
        List<IssuedSignatureVerificationResult> reports = checkIssuedSignatures(identifier, issuedSignatures);
        report.issuedSignatureVerificationResults = reports;
        if (requiredSigners != null && identifier != null) {
            List<JWS> relevantRequiredSigners = requiredSigners.getRequiredSignersAuthorizedOver(identifier);
            if (relevantRequiredSigners != null && !relevantRequiredSigners.isEmpty()) {
                report.chainNeedsRequiredSigner = true;
                report.chainGoodUpToRequiredSigner = areIssuedSignaturesTrustAndAuthorizedUpToRequiredSigner(relevantRequiredSigners, issuedSignatures, reports);
            }
        }
        if(issuedSignatures!=null){
            JWS rootSig = issuedSignatures.get(issuedSignatures.size() - 1).jws;
            IdentifierClaimsSet rootClaims = identifierVerifier.getIdentifierClaimsSet(rootSig);
            if (rootClaims == null) {
                return;
            }
            if (isRoot(rootClaims.sub, rootClaims.publicKey)) {
                report.rootIsTrusted = true;
            }
        }else{
            report.unableToBuildChain = true;
        }

    }

    private boolean areIssuedSignaturesTrustAndAuthorizedUpToRequiredSigner(List<JWS> relevantRequiredSigners, List<IssuedSignature> issuedSignatures, List<IssuedSignatureVerificationResult> reports) {
        // go up the chain
        for (int i = 0; i < issuedSignatures.size(); i++) {
            IssuedSignature sig = issuedSignatures.get(i);
            IssuedSignatureVerificationResult sigReport = reports.get(i);
            IdentifierClaimsSet chainClaims = identifierVerifier.getIdentifierClaimsSet(sig.jws);
            for (JWS requiredSigner : relevantRequiredSigners) {
                IdentifierClaimsSet requiredSignerClaims = identifierVerifier.getIdentifierClaimsSet(requiredSigner);
                // if you see an entity named in a relevant local cert, you are done
                if (Util.equalsPrefixCaseInsensitive(requiredSignerClaims.sub, chainClaims.iss) && requiredSignerClaims.publicKey.equals(sig.issuerPublicKey)) {
                    return true;
                }
            }
            // if not at a local cert entity, but permission not granted, the chain is bad, even if it reaches a local cert entity higher up
            if (!sigReport.canTrustAndAuthorized()) {
                return false;
            }
        }
        // never saw a local cert entity
        return false;
    }

    private List<IssuedSignatureVerificationResult> checkIssuedSignatures(String handle, List<IssuedSignature> issuedSignatures) {
        List<IssuedSignatureVerificationResult> result = new ArrayList<>();
        if (issuedSignatures != null) {
            for (IssuedSignature issuedSignature : issuedSignatures) {
                IssuedSignatureVerificationResult report = identifierVerifier.verifyIssuedSignature(handle, issuedSignature);
                result.add(report);
            }
        }
        return result;
    }

    private boolean isRoot(String subject, PublicKey publicKey) {
        if (rootKeys == null) {
            System.err.println("Error missing root keys.");
        }
        return Util.equalsCaseInsensitive(TRUST_ROOT_IDENTIFIER, ValueReference.transStr2ValueReference(subject).getIdentifierAsString()) && rootKeys.contains(publicKey);
    }

}