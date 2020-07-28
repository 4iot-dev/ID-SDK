package cn.ac.caict.iiiiot.id.client.security.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.adapter.IDAdapterFactory;
import cn.ac.caict.iiiiot.id.client.adapter.IdentifierAdapterException;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import org.junit.Test;

public class TrustTest {
    @Test
    public void certTrust() throws IdentifierAdapterException {
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        String[] types = new String[]{"HS_PUBKEY","HS_CERT"};
        IdentifierValue[] values= idAdapter.resolve("88.300.15907541011/0.88.300.15907541011",types,null);
        for(int i=0;i<values.length;i++){
            System.out.println(values[i].toString());
        }
    }
}
