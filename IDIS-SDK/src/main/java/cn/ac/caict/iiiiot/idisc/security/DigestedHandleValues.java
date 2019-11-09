/**********************************************************************\
 © COPYRIGHT 2015 Corporation for National Research Initiatives (CNRI);
                        All rights reserved.

        The HANDLE.NET software is made available subject to the
      Handle.Net Public License Agreement, which may be obtained at
          http://hdl.handle.net/20.1000/103 or hdl:20.1000/103
\**********************************************************************/

package cn.ac.caict.iiiiot.idisc.security;

import java.util.List;

public class DigestedHandleValues {
    public String alg;
    public List<DigestedHandleValue> digests;
    
    public static class DigestedHandleValue {
        public int index;
        public String digest;
        
        public DigestedHandleValue() { }
        
        public DigestedHandleValue(int index, String digest) {
            this.index = index;
            this.digest = digest;
        }
    }
}
