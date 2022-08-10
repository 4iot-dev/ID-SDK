package cn.ac.caict.iiiiot.id.client.adapter.trust;

public class IssuedSignatureVerificationResult extends SignatureVerificationResult {

    // permission granted
    public Boolean authorized;

    public boolean canTrustAndAuthorized() {
        return canTrust() && authorized != null && authorized;
    }
}