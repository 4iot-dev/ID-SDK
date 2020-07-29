package cn.ac.caict.iiiiot.id.client.adapter.trust;

import java.util.List;

public class DigestedHandleValues {
    public String alg;
    public List<DigestedHandleValue> digests;

    public static class DigestedHandleValue {
        public int index;
        public String digest;

        public DigestedHandleValue() {
        }

        public DigestedHandleValue(int index, String digest) {
            this.index = index;
            this.digest = digest;
        }
    }
}