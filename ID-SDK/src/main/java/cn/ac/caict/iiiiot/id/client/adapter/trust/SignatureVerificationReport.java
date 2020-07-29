package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.ArrayList;
import java.util.List;

public class SignatureVerificationReport {
    public boolean validPayload;
    public boolean signatureVerifies;
    public boolean dateInRange;
    public String sub;
    public String iss;

    public List<Exception> exceptions = new ArrayList<>();

    public boolean canTrust() {
        return validPayload && signatureVerifies && dateInRange;
    }
}