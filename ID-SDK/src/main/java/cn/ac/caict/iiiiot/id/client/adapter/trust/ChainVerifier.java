package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import org.apache.commons.codec.binary.StringUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ChainVerifier {
    private static final String TRUST_ROOT_IDENTIFIER = "88.300.15907541011/0.0";
    private final IdentifierVerifier identifierVerifier = new IdentifierVerifier();

    private final List<PublicKey> rootKeys;
    private AbstractRequiredSignerStore requiredSigners;

    public ChainVerifier(List<PublicKey> rootKeys) {
        this.rootKeys = rootKeys;
    }

    public ChainVerifier(List<PublicKey> rootKeys, AbstractRequiredSignerStore requiredSigners) {
        this.rootKeys = rootKeys;
        this.requiredSigners = requiredSigners;
    }

    public ChainVerificationReport verifyValues(String identifier, List<IdentifierValue> values, List<IssuedSignature> issuedSignatures) {
        ChainVerificationReport report = new ChainVerificationReport();
        ValuesSignatureVerificationReport valuesReport = identifierVerifier.verifyValues(identifier, values, issuedSignatures.get(0).jws, issuedSignatures.get(0).issuerPublicKey);
        report.valuesReport = valuesReport;
        setChainReportValues(report, identifier, issuedSignatures);
        return report;
    }

    public ChainVerificationReport verifyChain(List<IssuedSignature> issuedSignatures) {
        ChainVerificationReport report = new ChainVerificationReport();
        setChainReportValues(report, null, issuedSignatures);
        return report;
    }

    private void setChainReportValues(ChainVerificationReport report, String identifier, List<IssuedSignature> issuedSignatures) {
        List<IssuedSignatureVerificationReport> reports = checkIssuedSignatures(identifier, issuedSignatures);
        report.issuedSignatureVerificationReports = reports;
        if (requiredSigners != null && identifier != null) {
            List<JsonWebSignature> relevantRequiredSigners = requiredSigners.getRequiredSignersAuthorizedOver(identifier);
            if (relevantRequiredSigners != null && !relevantRequiredSigners.isEmpty()) {
                report.chainNeedsRequiredSigner = true;
                report.chainGoodUpToRequiredSigner = areIssuedSignaturesTrustAndAuthorizedUpToRequiredSigner(relevantRequiredSigners, issuedSignatures, reports);
            }
        }
        if(issuedSignatures!=null){
            JsonWebSignature rootSig = issuedSignatures.get(issuedSignatures.size() - 1).jws;
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

    private boolean areIssuedSignaturesTrustAndAuthorizedUpToRequiredSigner(List<JsonWebSignature> relevantRequiredSigners, List<IssuedSignature> issuedSignatures, List<IssuedSignatureVerificationReport> reports) {
        // go up the chain
        for (int i = 0; i < issuedSignatures.size(); i++) {
            IssuedSignature sig = issuedSignatures.get(i);
            IssuedSignatureVerificationReport sigReport = reports.get(i);
            IdentifierClaimsSet chainClaims = identifierVerifier.getIdentifierClaimsSet(sig.jws);
            for (JsonWebSignature requiredSigner : relevantRequiredSigners) {
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

    private List<IssuedSignatureVerificationReport> checkIssuedSignatures(String handle, List<IssuedSignature> issuedSignatures) {
        List<IssuedSignatureVerificationReport> result = new ArrayList<>();
        if (issuedSignatures != null) {
            for (IssuedSignature issuedSignature : issuedSignatures) {
                IssuedSignatureVerificationReport report = identifierVerifier.verifyIssuedSignature(handle, issuedSignature);
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