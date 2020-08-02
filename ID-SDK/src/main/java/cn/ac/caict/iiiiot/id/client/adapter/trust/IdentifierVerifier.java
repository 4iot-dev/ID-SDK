package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import cn.ac.caict.iiiiot.id.client.adapter.trust.DigestedIdentifierValues.DigestedIdentifierValue;

public class IdentifierVerifier {
    private static IdentifierVerifier INSTANCE = new IdentifierVerifier();

    public static IdentifierVerifier getInstance() {
        return INSTANCE;
    }

    public ValuesSignatureVerificationResult verifyValues(String identifier, List<IdentifierValue> values, JWS signature, PublicKey publicKey) {
        ValuesSignatureVerificationResult result = new ValuesSignatureVerificationResult();
        verifyIdentifierClaimsSetAndSetReportProperties(result, signature, publicKey);
        IdentifierClaimsSet claims = getIdentifierClaimsSet(signature);
        if (claims == null) return result;

        result.correctHandle = Util.equalsPrefixCaseInsensitive(identifier, claims.sub);

        if (claims.digests == null || claims.digests.alg == null) {
            result.validPayload = false;
            return result;
        }

        DigestedIdentifierValues digestedIdentifierValues;
        try {
            digestedIdentifierValues = new IdentifierValueDigester().digest(values, claims.digests.alg);
        } catch (NoSuchAlgorithmException e) {
            result.validPayload = false;
            result.exceptions.add(e);
            return result;
        }

        result.verifiedValues = getVerifiedValues(digestedIdentifierValues.digests, claims.digests.digests);
        result.unsignedValues = getUnsignedValues(digestedIdentifierValues.digests, claims.digests.digests);
        result.badDigestValues = getBadDigestValues(digestedIdentifierValues.digests, claims.digests.digests);
        result.missingValues = getMissingValues(digestedIdentifierValues.digests, claims.digests.digests);
        result.iss = claims.iss;
        result.sub = claims.sub;
        return result;
    }

    public IdentifierClaimsSet getIdentifierClaimsSet(JWS signature) {
        IdentifierClaimsSet claims = null;
        try {
            String payload = signature.getPayloadAsString();
            claims = GsonCompose.getGson().fromJson(payload, IdentifierClaimsSet.class);
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    public void verifyIdentifierClaimsSetAndSetReportProperties(SignatureVerificationResult result, JWS signature, PublicKey publicKey) {
        try {
            result.signatureVerifies = signature.validates(publicKey);
        } catch (Exception e) {
            result.signatureVerifies = false;
            result.exceptions.add(e);
        }

        IdentifierClaimsSet claims;
        try {
            String payload = signature.getPayloadAsString();
            claims = GsonCompose.getGson().fromJson(payload, IdentifierClaimsSet.class);
            result.validPayload = true;
        } catch (Exception e) {
            result.validPayload = false;
            result.exceptions.add(e);
            return;
        }

        long nowInSeconds = System.currentTimeMillis() / 1000L;
        result.dateInRange = claims.isDateInRange(nowInSeconds);
    }

    List<Integer> getBadDigestValues(List<DigestedIdentifierValues.DigestedIdentifierValue> actual, List<DigestedIdentifierValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) return result;
        for (DigestedIdentifierValue actualDigest : actual) {
            for (DigestedIdentifierValue claimedDigest : claimedDigests) {
                if (actualDigest.index == claimedDigest.index) {
                    if (!actualDigest.digest.equals(claimedDigest.digest)) {
                        result.add(actualDigest.index);
                        break;
                    }
                }
            }
        }
        return result;
    }

    List<Integer> getVerifiedValues(List<DigestedIdentifierValue> actual, List<DigestedIdentifierValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) return result;
        for (DigestedIdentifierValue actualDigest : actual) {
            for (DigestedIdentifierValue claimedDigest : claimedDigests) {
                if (actualDigest.index == claimedDigest.index) {
                    if (actualDigest.digest.equals(claimedDigest.digest)) {
                        result.add(actualDigest.index);
                        break;
                    }
                }
            }
        }
        return result;
    }

    List<Integer> getUnsignedValues(List<DigestedIdentifierValue> actual, List<DigestedIdentifierValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) {
            for (DigestedIdentifierValue actualDigest : actual) {
                result.add(actualDigest.index);
            }
            return result;
        }

        for (DigestedIdentifierValue actualDigest : actual) {
            boolean found = false;
            for (DigestedIdentifierValue claimedDigest : claimedDigests) {
                if (actualDigest.index == claimedDigest.index) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(actualDigest.index);
            }
        }
        return result;
    }

    List<Integer> getMissingValues(List<DigestedIdentifierValue> actual, List<DigestedIdentifierValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) {
            return result;
        }

        for (DigestedIdentifierValue claimedDigest : claimedDigests) {
            boolean found = false;
            for (DigestedIdentifierValue actualDigest : actual) {
                if (actualDigest.index == claimedDigest.index) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(claimedDigest.index);
            }
        }
        return result;
    }

    public void verifyIssuedSignatureIsValid(IssuedSignature issuedSignature, SignatureVerificationResult report) {
        verifyIdentifierClaimsSetAndSetReportProperties(report, issuedSignature.jws, issuedSignature.issuerPublicKey);
    }

    public boolean verifyPermissionsAreAuthorizedOverIdentifier(String identifier, List<Permission> perms) {
        if (perms == null || perms.isEmpty()) return false;
        for (Permission permission : perms) {
            if (Permission.EVERYTHING.equals(permission.perm)) {
                return true;
            } else if (Permission.THIS_IDENTIFER.equals(permission.perm)) {
                if (Util.equalsPrefixCaseInsensitive(identifier, permission.identifier) || Util.isIdentifierUnderPrefix(identifier, permission.identifier)) {
                    return true;
                }
            } else if (Permission.DERIVED_PREFIXES.equals(permission.perm)) {
                if (Util.isDerivedFrom(identifier, permission.identifier) || Util.isDerivedFrom(Util.getZeroNAIdentifier(identifier), permission.identifier)) {
                    return true;
                }
            } else if (Permission.IDENTIFIERS_UNDER_THIS_PREFIX.equals(permission.perm)) {
                if (Util.isIdentifierUnderPrefix(identifier, permission.identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void verifyIssuedSignatureIsAuthorizedOverIdentifier(String identifier, IssuedSignature issuedSignature, IssuedSignatureVerificationResult report) {
        boolean verified = verifyPermissionsAreAuthorizedOverIdentifier(identifier, issuedSignature.issuerPermissions);
        report.authorized = verified;
    }

    public IssuedSignatureVerificationResult verifyIssuedSignature(String identifier, IssuedSignature issuedSignature) {
        IssuedSignatureVerificationResult report = new IssuedSignatureVerificationResult();
        IdentifierClaimsSet claims = getIdentifierClaimsSet(issuedSignature.jws);
        report.iss = claims.iss;
        report.sub = claims.sub;
        verifyIssuedSignatureIsValid(issuedSignature, report);
        if (identifier != null) verifyIssuedSignatureIsAuthorizedOverIdentifier(identifier, issuedSignature, report);
        return report;
    }

}