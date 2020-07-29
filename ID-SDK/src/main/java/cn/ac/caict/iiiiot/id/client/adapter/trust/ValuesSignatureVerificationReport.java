package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.ArrayList;
import java.util.List;

public class ValuesSignatureVerificationReport extends SignatureVerificationReport {

    public boolean correctHandle;
    public List<Integer> verifiedValues;
    public List<Integer> missingValues; //present in signature digests missing in values
    public List<Integer> unsignedValues; //present in values missing in signature digests
    public List<Integer> badDigestValues;

    public ValuesSignatureVerificationReport() {
        this.verifiedValues = new ArrayList<>();
        this.missingValues = new ArrayList<>();
        this.unsignedValues = new ArrayList<>();
        this.badDigestValues = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }
}