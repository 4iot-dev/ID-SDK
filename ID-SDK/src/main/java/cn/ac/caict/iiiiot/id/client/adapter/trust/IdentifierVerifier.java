package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import cn.ac.caict.iiiiot.id.client.adapter.trust.DigestedHandleValues.DigestedHandleValue;

public class IdentifierVerifier {
    private static IdentifierVerifier INSTANCE = new IdentifierVerifier();

    public static IdentifierVerifier getInstance() {
        return INSTANCE;
    }

    public ValuesSignatureVerificationReport verifyValues(String handle, List<IdentifierValue> values, JsonWebSignature signature, PublicKey publicKey) {
        ValuesSignatureVerificationReport report = new ValuesSignatureVerificationReport();
        verifyHandleClaimsSetAndSetReportProperties(report, signature, publicKey);
        IdentifierClaimsSet claims = getIdentifierClaimsSet(signature);
        if (claims == null) return report;

        report.correctHandle = Util.equalsPrefixCaseInsensitive(handle, claims.sub);

        if (claims.digests == null || claims.digests.alg == null) {
            report.validPayload = false;
            return report;
        }

        DigestedHandleValues digestedHandleValues;
        try {
            digestedHandleValues = new HandleValueDigester().digest(values, claims.digests.alg);
        } catch (NoSuchAlgorithmException e) {
            report.validPayload = false;
            report.exceptions.add(e);
            return report;
        }

        report.verifiedValues = getVerifiedValues(digestedHandleValues.digests, claims.digests.digests);
        report.unsignedValues = getUnsignedValues(digestedHandleValues.digests, claims.digests.digests);
        report.badDigestValues = getBadDigestValues(digestedHandleValues.digests, claims.digests.digests);
        report.missingValues = getMissingValues(digestedHandleValues.digests, claims.digests.digests);
        report.iss = claims.iss;
        report.sub = claims.sub;
        return report;
    }

    public IdentifierClaimsSet getIdentifierClaimsSet(JsonWebSignature signature) {
        IdentifierClaimsSet claims = null;
        try {
            String payload = signature.getPayloadAsString();
            claims = GsonUtility.getGson().fromJson(payload, IdentifierClaimsSet.class);
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    public void verifyHandleClaimsSetAndSetReportProperties(SignatureVerificationReport report, JsonWebSignature signature, PublicKey publicKey) {
        try {
            report.signatureVerifies = signature.validates(publicKey);
        } catch (Exception e) {
            report.signatureVerifies = false;
            report.exceptions.add(e);
        }

        IdentifierClaimsSet claims;
        try {
            String payload = signature.getPayloadAsString();
            claims = GsonUtility.getGson().fromJson(payload, IdentifierClaimsSet.class);
            report.validPayload = true;
        } catch (Exception e) {
            report.validPayload = false;
            report.exceptions.add(e);
            return;
        }

        long nowInSeconds = System.currentTimeMillis() / 1000L;
        report.dateInRange = claims.isDateInRange(nowInSeconds);
    }

    List<Integer> getBadDigestValues(List<DigestedHandleValues.DigestedHandleValue> actual, List<DigestedHandleValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) return result;
        for (DigestedHandleValue actualDigest : actual) {
            for (DigestedHandleValue claimedDigest : claimedDigests) {
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

    List<Integer> getVerifiedValues(List<DigestedHandleValue> actual, List<DigestedHandleValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) return result;
        for (DigestedHandleValue actualDigest : actual) {
            for (DigestedHandleValue claimedDigest : claimedDigests) {
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

    List<Integer> getUnsignedValues(List<DigestedHandleValue> actual, List<DigestedHandleValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) {
            for (DigestedHandleValue actualDigest : actual) {
                result.add(actualDigest.index);
            }
            return result;
        }

        for (DigestedHandleValue actualDigest : actual) {
            boolean found = false;
            for (DigestedHandleValue claimedDigest : claimedDigests) {
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

    List<Integer> getMissingValues(List<DigestedHandleValue> actual, List<DigestedHandleValue> claimedDigests) {
        List<Integer> result = new ArrayList<>();
        if (claimedDigests == null) {
            return result;
        }

        for (DigestedHandleValue claimedDigest : claimedDigests) {
            boolean found = false;
            for (DigestedHandleValue actualDigest : actual) {
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

    public void verifyIssuedSignatureIsValid(IssuedSignature issuedSignature, SignatureVerificationReport report) {
        verifyHandleClaimsSetAndSetReportProperties(report, issuedSignature.jws, issuedSignature.issuerPublicKey);
    }

    public boolean verifyPermissionsAreAuthorizedOverHandle(String handle, List<Permission> perms) {
        if (perms == null || perms.isEmpty()) return false;
        for (Permission permission : perms) {
            if (Permission.EVERYTHING.equals(permission.perm)) {
                return true;
            } else if (Permission.THIS_IDENTIFER.equals(permission.perm)) {
                if (Util.equalsPrefixCaseInsensitive(handle, permission.identifier) || Util.isHandleUnderPrefix(handle, permission.identifier)) {
                    return true;
                }
            } else if (Permission.DERIVED_PREFIXES.equals(permission.perm)) {
                if (Util.isDerivedFrom(handle, permission.identifier) || Util.isDerivedFrom(Util.getZeroNAHandle(handle), permission.identifier)) {
                    return true;
                }
            } else if (Permission.IDENTIFIERS_UNDER_THIS_PREFIX.equals(permission.perm)) {
                if (Util.isHandleUnderPrefix(handle, permission.identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void verifyIssuedSignatureIsAuthorizedOverHandle(String handle, IssuedSignature issuedSignature, IssuedSignatureVerificationReport report) {
        boolean verified = verifyPermissionsAreAuthorizedOverHandle(handle, issuedSignature.issuerPermissions);
        report.authorized = verified;
    }

    public IssuedSignatureVerificationReport verifyIssuedSignature(String handle, IssuedSignature issuedSignature) {
        IssuedSignatureVerificationReport report = new IssuedSignatureVerificationReport();
        IdentifierClaimsSet claims = getIdentifierClaimsSet(issuedSignature.jws);
        report.iss = claims.iss;
        report.sub = claims.sub;
        verifyIssuedSignatureIsValid(issuedSignature, report);
        if (handle != null) verifyIssuedSignatureIsAuthorizedOverHandle(handle, issuedSignature, report);
        return report;
    }

}