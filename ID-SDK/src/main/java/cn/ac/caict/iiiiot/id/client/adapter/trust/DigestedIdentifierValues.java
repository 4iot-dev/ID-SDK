package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.List;

public class DigestedIdentifierValues {
    public String alg;
    public List<DigestedIdentifierValue> digests;

    public static class DigestedIdentifierValue {
        public int index;
        public String digest;

        public DigestedIdentifierValue() {
        }

        public DigestedIdentifierValue(int index, String digest) {
            this.index = index;
            this.digest = digest;
        }
    }
}