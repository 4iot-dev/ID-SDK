package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.adapter.trust.*;
import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.IdentifierValueUtil;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IDAdapterTest {
    @Ignore
    @Test
    public void resolveTest() throws IdentifierAdapterException, IdentifierException, InterruptedException {
        //        IdentifierValue[] values = IDAdapterFactory.newInstance().resolve("88.111.1/test test");


        // IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
        // PrefixSiteInfo prefixSiteInfo = idAdapter.resolveSiteByProxy("88.111.1");
        // long begin = System.currentTimeMillis();
        // IDAdapter adapter = IDAdapterFactory.newInstance(prefixSiteInfo.getIp(), prefixSiteInfo.getPort());
        // System.out.println((System.currentTimeMillis() - begin));

        //        IDAdapterFactory.newInstance("88.55").resolve("88.55.1");
        IDAdapter idAdapter1 = IDAdapterFactory.newInstance("139.198.126.227", 2441);
        idAdapter1.resolve("88.902.000000");
        Thread.sleep(1000l);
    }

    @Ignore
    @Test
    public void resolveTest1() throws IdentifierAdapterException, IdentifierException, InterruptedException {
        //        IdentifierValue[] values = IDAdapterFactory.newInstance().resolve("88.111.1/test test");


        // IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
        // PrefixSiteInfo prefixSiteInfo = idAdapter.resolveSiteByProxy("88.111.1");
        // long begin = System.currentTimeMillis();
        // IDAdapter adapter = IDAdapterFactory.newInstance(prefixSiteInfo.getIp(), prefixSiteInfo.getPort());
        // System.out.println((System.currentTimeMillis() - begin));

        //        IDAdapterFactory.newInstance("88.55").resolve("88.55.1");
        // IDAdapter idAdapter1 = IDAdapterFactory.newInstance("139.198.126.227", 2645);
        // idAdapter1.resolve("88.902.6688/1111");

        IDAdapter idAdapter2 = IDAdapterFactory.newInstance("88.111.10086");
        idAdapter2.resolve("88.111.10086/Test_A");
    }

    public static void main(String[] args) throws IdentifierAdapterException, InterruptedException {
        while (true) {
            IDAdapter idAdapter = IDAdapterFactory.newInstance("139.198.126.227", 2441);
            idAdapter.resolve("88.902.000000");
            Thread.sleep(1000l);
        }
    }

    @Ignore
    @Test
    public void resolveCert() throws IdentifierAdapterException, IdentifierTrustException {
        IdentifierValue[] values = IDAdapterFactory.cachedInstance().resolve("88");
        IdentifierValue[] certValues = ValueHelper.getInstance().filter(values, "HS_CERT");
        if (certValues.length > 0) {
            IdentifierValue cert = certValues[0];
            IdentifierClaimsSet claims = ValueHelper.getInstance().getIdentifierClaimsSet(cert);
            System.out.println(GsonCompose.getPrettyGson().toJson(claims));
        }
    }

    @Ignore
    @Test
    public void resolveSignature() throws IdentifierAdapterException, IdentifierTrustException {
        IdentifierValue[] values = IDAdapterFactory.cachedInstance().resolve("88.300.15907541011/1038");
        IdentifierValue[] certValues = ValueHelper.getInstance().filter(values, Common.HS_SIGNATURE);
        if (certValues.length > 0) {
            IdentifierValue cert = certValues[0];
            IdentifierClaimsSet claims = ValueHelper.getInstance().getIdentifierClaimsSet(cert);
            System.out.println(GsonCompose.getPrettyGson().toJson(claims));
        }
    }

    @Ignore
    @Test
    public void resolveSite() throws IdentifierAdapterException, IdentifierException {
        IdentifierValue[] valueArray = IDAdapterFactory.cachedInstance().resolve("88.300.15907541011", null, null);
        if (valueArray.length > 0) {
            IdentifierValue iv = valueArray[0];
            SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
            ServerInfo[] servers = siteInfo.servers;

            if (servers.length > 0) {

                ServerInfo serverInfo = servers[0];
                IDCommunicationItems tcpItem = ValueHelper.getInstance().findFirstByProtocolName(serverInfo, "TCP");

                System.out.println(serverInfo.getAddressStr());
                System.out.println(siteInfo.isPrimarySite);
                System.out.println(tcpItem.getPort());
            } else {
                throw new IdentifierAdapterException("cannot find servers");
            }
        }

    }

    @Ignore
    @Test
    public void resolve() throws IdentifierAdapterException, IdentifierTrustException {
        try {
            IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
            IdentifierValue[] values = idAdapter.resolve("88.111.1001/1111111");
            //            List<IdentifierValue> valueList = new ArrayList<>();
            //            valueList.add(new IdentifierValue(1, "URL", "https://www.citln.cn/"));
            //            valueList.add(new IdentifierValue(2, "EMAIL", "test@email.com"));
            //            idAdapter.createIdentifier("88.167.14/1",ValueHelper.getInstance().listToArray(valueList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Ignore
    @Test
    public void resolveAuth() throws IdentifierAdapterException {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC0gT2YeLElI3XtUrxUImvfofx8\n" +
                "QrsdEzfiV6WMQVSYTi+fsbBj2PckU3loy6DxdCIn0lQ0Pi0/3DJL5fdf1kSf0xB42VQRIgMlBCOZ\n" +
                "MNN+D2QdDU+jf0FMHyCVceh/0S3NDbdH3ynbADvZnSV2Y8SVnHL7vKU4TTjF4QoR4XVcttQ1Bgsn\n" +
                "euYB4aN9KCpWT5VrZaZqSswGJl0YlOpA0EU/rKisQ7XnBxNNCa85NudEPrxWVqno6xA6Q7ARJBXE\n" +
                "e5v/KX/O8duTQc5PlYZZ+PuI+32GYFw84vioIVKJABcUQWPRzj8k4ix8r0pIeQ5i+rv0dzQ2mr0t\n" +
                "pruPWZ5s15aNAgMBAAECggEAZGc3QeZwcr4EzJum0g3Lfzt1XBEqT4PvI+Go3hlA2u8yoluOSBvZ\n" +
                "bMVZ1SbfQS9eCFSALgcf2FO9TmZ+cTqYNWaL1QzeLCGFKkPhIUb9fTNGdrp+v+z6/0KZN0eDEgXi\n" +
                "uhqVBk9l0sGQiP1WZ0IbiTTa6JgINwcNO1Rv635Db+v4gQpYePnZYqwSu7x2fuEI33Taorrfucv8\n" +
                "M7Zi49fSWq81tx+JN3otFHkyMD11eAYV/aZjwpwscpA8ruYlzzqYp7aMxxgQ4AXJYu71DYp9Hgz2\n" +
                "aQM2dXlAKgXCmrRtJq9DrPw4CMX7NHTAQjlvST+vu/Ja8H+23sRy/xH5k+DaKQKBgQD7tsrjle35\n" +
                "aPyGIDej+VCLOH8dFrIZeh9U0bV+/TL2R1mC/P2i9WVFHNAviNB+qU8DU6tfLWj8dpiZx6BKR4Qa\n" +
                "h4v7Oivjww/ru/5WiClfslsHrOMjiWy/N5ZSk6NQnHAsJncVkT4XIErsc1LaZnccOMwfXW1xVcCN\n" +
                "maAt1xl9HwKBgQC3lA0apt5iG+IkLR8jj4UPVfyy7llLb+zdx0wGh/PziGqgt6ZdY8t8w66hWFb5\n" +
                "n96POmW7DuFaCnNnTiwFpz1d6rwI0XhlrGOmp7NjstvZ+Fk4ap6qpOKSIB8LeN+bOrtx7TZ1UKG1\n" +
                "wLsFD40H2hUxymalwnm4Vp5pJjccW4XK0wKBgADDqlQMlX9nYTTrDiAyVptFnaUx93J6W3P/ewSa\n" +
                "sjfrOYtbR03iXt9Z2gv6518rFnFVJLUSRzpVBduZrpPrKayG8tbdc1qqsfauSHRsz2tZ+ErKrJnk\n" +
                "Be+CtLMlfZ52CyUnLL9lBII/d9rF8t905jGwvnXt67InZ4FGkSTyfUJTAoGAUKrP70wwIDBceMUT\n" +
                "D887Cvgf6Ihv2IRAM1wl/iCzg+oH4MOSaSs2+YYLMH7fCSXE6G8i0MXDJIu/Fj/1fC52+tPw+HcD\n" +
                "Tront82tODwZ+3fzzKSdQCLgJJHU0ne02kM+ptszuO1LgdBE3f5tXGvqMEzeOixwzB3T0iSmxuE4\n" +
                "s10CgYBt6o1BD86oapY92dMMfvYiURskar606pCH7m0y8aQfRQmrM3z5+YWnpbEMbNJsr8uS558X\n" +
                "whhWREGhoDnaowcJ9A8tenDUgDNf98pDKpeudMLG+32YaHPtxWOBTywLVpbh4I1sykLJnwAq+Lve\n" +
                "KMLphT7U+sCwvAKmOQ6vGLhXsg==\n" +
                "-----END PRIVATE KEY-----";
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5647, "88.300.15907541011", 300, privateKeyPem, 1);
        idAdapter.resolve("88.300.15907541011/user002", null, null, true);
    }


}