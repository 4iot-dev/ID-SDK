package cn.ac.caict.iiiiot.id.client.adapter.trust;

public class IssuedSignatureVerificationReport extends SignatureVerificationReport {

    // permission granted
    public Boolean authorized;

    public boolean canTrustAndAuthorized() {
        return canTrust() && authorized != null && authorized;
    }
}