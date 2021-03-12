package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.security.Permission;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public abstract class RequiredSigners {

    private static IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
    protected volatile List<JWS> requiredSigners;

    public void loadSigners() {
        //no-op
    }

    public boolean needsLoadSigners() {
        return false;
    }

    protected boolean validateSelfSignedCert(JWS cert) throws IdentifierTrustException {
        IdentifierClaimsSet claims = identifierVerifier.getIdentifierClaimsSet(cert);
        PublicKey publicKey = claims.publicKey;
        String issuer = claims.iss;
        String subject = claims.sub;
        if (!issuer.equals(subject)) {
            return false;
        }
        if (!claims.isDateInRange(System.currentTimeMillis() / 1000L)) {
            return false;
        }
        return cert.validates(publicKey);
    }

    public List<JWS> getRequiredSignersAuthorizedOver(String identifier) {
        List<JWS> currentRequiredSigners = requiredSigners;
        List<JWS> results = new ArrayList<>();
        for (JWS cert : currentRequiredSigners) {
            IdentifierClaimsSet claims = identifierVerifier.getIdentifierClaimsSet(cert);
            List<Permission> perms = claims.perms;
            boolean isAuthorizedOver = identifierVerifier.verifyPermissionsAreAuthorizedOverIdentifier(identifier, perms);
            if (isAuthorizedOver) {
                results.add(cert);
            }
        }
        return results;
    }

}