package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import sun.jvm.hotspot.utilities.Assert;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class IDUserAuthTest {

    /**
     * 注册用户标识
     * @throws Exception
     */
    @Test
    public void test1InitUser() throws Exception {
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);

        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newAdminValue(100,"88.300.15907541011/group",200);
        ValueReference vr= new ValueReference("88.300.15907541011/user002",300);
        IdentifierValue vlist = valueHelper.newVListValue(200,new ValueReference[]{vr});


        // String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
        //         "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtIE9mHixJSN17VK8VCJr36H8fEK7HRM3\n" +
        //         "4leljEFUmE4vn7GwY9j3JFN5aMug8XQiJ9JUND4tP9wyS+X3X9ZEn9MQeNlUESIDJQQjmTDTfg9k\n" +
        //         "HQ1Po39BTB8glXHof9EtzQ23R98p2wA72Z0ldmPElZxy+7ylOE04xeEKEeF1XLbUNQYLJ3rmAeGj\n" +
        //         "fSgqVk+Va2WmakrMBiZdGJTqQNBFP6yorEO15wcTTQmvOTbnRD68Vlap6OsQOkOwESQVxHub/yl/\n" +
        //         "zvHbk0HOT5WGWfj7iPt9hmBcPOL4qCFSiQAXFEFj0c4/JOIsfK9KSHkOYvq79Hc0Npq9Laa7j1me\n" +
        //         "bNeWjQIDAQAB\n" +
        //         "-----END PUBLIC KEY-----";
        // PublicKey publicKey = KeyConverter.fromX509Pem(publicKeyPem);
        // values[1] = valueHelper.newPublicKeyValue(300,publicKey);
        // idAdapter.deleteIdentifier("88.300.15907541011/user002");
        idAdapter.createIdentifier("88.300.15907541011/user002", values);

    }

    @Ignore
    @Test
    public void test2ModUser() throws Exception {
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 2642);
        IdentifierValue[] result = idAdapter.resolve("88.300.15907541011",null,null);

        List<IdentifierValue> toRemove = new ArrayList<IdentifierValue>();
        for(int i=0;i<result.length;i++){
            System.out.println(result[i].toString());
            if(!result[i].getTypeStr().equals("HS_SITE")){
                toRemove.add(result[i]);
            }
        }
        if(!toRemove.isEmpty()){
            System.out.println(toRemove.size());
            IdentifierValue[] toRemoveArray = new IdentifierValue[2];
            toRemove.toArray(toRemoveArray);
            idAdapter.deleteIdentifierValues("88.300.15907541011",toRemoveArray);
        }


        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newAdminValue(100,"88.300.15907541011",300);

        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtIE9mHixJSN17VK8VCJr36H8fEK7HRM3\n" +
                "4leljEFUmE4vn7GwY9j3JFN5aMug8XQiJ9JUND4tP9wyS+X3X9ZEn9MQeNlUESIDJQQjmTDTfg9k\n" +
                "HQ1Po39BTB8glXHof9EtzQ23R98p2wA72Z0ldmPElZxy+7ylOE04xeEKEeF1XLbUNQYLJ3rmAeGj\n" +
                "fSgqVk+Va2WmakrMBiZdGJTqQNBFP6yorEO15wcTTQmvOTbnRD68Vlap6OsQOkOwESQVxHub/yl/\n" +
                "zvHbk0HOT5WGWfj7iPt9hmBcPOL4qCFSiQAXFEFj0c4/JOIsfK9KSHkOYvq79Hc0Npq9Laa7j1me\n" +
                "bNeWjQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        PublicKey publicKey = KeyConverter.fromX509Pem(publicKeyPem);
        values[1] = valueHelper.newPublicKeyValue(300,publicKey);
        idAdapter.addIdentifierValues("88.300.15907541011", values);
    }

    @Test
    public void test3Login() throws IOException {
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
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37",5647,"88.300.15907541011",300,privateKeyPem,1);

    }

    @Test
    public void test4Value() throws Exception {

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        String identifier = "88.300.15907541011/1024";
        List<IdentifierValue> values = new ArrayList<>();

        values.add(new IdentifierValue(1, "URL", "https://www.citln.cn/"));
        values.add(new IdentifierValue(2, "EMAIL", "test@email.com"));

        IdentifierValue value =new IdentifierValue(3, "URL", "https://www.citln.cn/");
        value.setPublicRead(false);
        value.setPublicWrite(false);
        values.add(value);

        idAdapter.deleteIdentifier(identifier);
        idAdapter.createIdentifier(identifier, valueHelper.listToArray(values));
    }

    @Test
    public void test5NoLoginResolve() throws Exception {
        String identifier = "88.300.15907541011/1024";
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5647);
        IdentifierValue[] values = idAdapter.resolve(identifier);
        boolean hasIndex3 = false;
        for(IdentifierValue value:values){
            if(value.index==3){
                hasIndex3 = true;
            }
        }
        Assert.that(!hasIndex3,"不能查看管理员的值");
    }

    @Test
    public void test6LoginResolve() throws Exception {
        String identifier = "88.300.15907541011/1024";
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
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37",5647,"88.300.15907541011",300,privateKeyPem,1);
        IdentifierValue[] values = idAdapter.resolve(identifier);

        boolean hasIndex3 = false;
        for(IdentifierValue value:values){
            if(value.index==3){
                hasIndex3 = true;
            }
        }
        Assert.that(hasIndex3,"管理员可以查看管理员的值");
    }

    @Test
    public void test7LoginResolve() throws Exception {
        String identifier = "88.300.15907541011/1024";
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCWFZciDR7kgbe3hN6D97YCZyNE\n" +
                "hOgdjx3/xAjuip3S816L539+X6Og8y+eRT8SFrphTNnqom7mwx42nXGmxEOA5M5ZOmxmaUjNnuuk\n" +
                "hMD83KHvTItJ/qnCnmPvaorRpaqRZ8hu75CAIz+TZcTd/n7GHE0bBxuPrKEPc7BuZOydDhu3D9SE\n" +
                "pdlcq7XqYquzZ6qy72iWWY9SXro8thMduzsbESpg0bG6xaQctNqzWCh3S6+cHBBOuJh9awzS0qDL\n" +
                "+7LwbMcWITN6BrDJwJ/bTKK8xmyKPQM7Itfx1fgZIksLAjOv/uhPtvwvpJmJXM54C+IxXWgL2wqi\n" +
                "OqbkRGmi1OcNAgMBAAECggEAciq4TanHbzGRzCNhlOeQ56EHPe8QFrQwsMext6twRPDmYexWmo4s\n" +
                "DZoQBghM4vixDKUGO6sWS6nvSMJZmu8/HkRvB4cewISkYe3a+MoXeAJpmHwUlNQI1Mfh41SbhsDL\n" +
                "0FIvMNxBgofNq9VFfU1DqHYM3Vt5xsAdwIBTnJZfO1Q0uJu8GcE1oK0iKikc1fBHERMwT1vEuB/9\n" +
                "FZ/RsqbtRrKas2KunA48HsGLnEUKVm7Yd7UfNrq2WUUDN71wX6GMIXl6tstQPT6rLOKVd4i58qOF\n" +
                "TOsCjxvcDqOHYzWFVC0qKc8DAfiJKBse2lrBPPyy1RObvtNF45aQBXIcNtn2rQKBgQD+Re28xRTE\n" +
                "x2+XWtxv3XDC9G/kchIqIPtjFpcTGllIzqWvrfbAj3EtK6i8IwSzVhm4bK/f9QklwdXCvBa2Q7Ib\n" +
                "FYwJ49TcLOUgPGR6XoeewTfxPtd7DHYc6Qf4lLvcXRNhTAYAUo38MCU/tFz4s2EzNSMvoiNZs0SG\n" +
                "gvRa8dnx8wKBgQCXGoW0csug+iK2NCTUy2/beC1BTOw7Q/FR1LhTx5YZUSEcW1C13Yi1TBBC87e0\n" +
                "+M6DwCRcCl53oGgIAvsoz3saFLi8VRaeEgGOMTzPbXyOjnbTFPaGLIQN/USrigYF9K/Wj3NEKKrD\n" +
                "Y+xSsRoWdiVzANtlhcaPCEEtjTZFSwMC/wKBgQCLHZdEgE8smjDjjkKssWrQPww44HAvvHvNar6d\n" +
                "JP1UaDseMv+dut0g6ZIX3NDljWXlERHoZhfrIHHjaSTD86WuS8uiXZRIIgf56xDOIonwwaXpx4f9\n" +
                "OoQkrRo/VqCxn/rKcN2Xr5MfNvgqMnPrXbkAW0I0/XB50yrarSsj1s53IQKBgEh30N4dHCnlLRaj\n" +
                "ZXKk6s2gElw6DyXRTQBI++o6U3Nq5G1cIhZG3BTPGRFzH4zssuafFRELbAkFZj9JZrlI4Ko+e+bf\n" +
                "DAzdZ9+RH6//u3gHPVe4uw7YdLDc4b0/YeojUt2dSNPMiTFST/nMtE1Hdmh0ZcSH5xWcCN5W1ktf\n" +
                "UV9zAoGALT1J6TKJmNEOY6Im8omUHJXKdY7BarFynBTAKupBkFZb7LHayPmmmCvbiEgGVJa8MsXD\n" +
                "5qEZFbHm05VePrZnjczm+u+NmHTkWHn4dB/Uh2AhVD68+ZcY7ZT3N7BI6EVK0kRpe0//UfTXS4Lv\n" +
                "fMMMSyAQeTw6sZsqQau+1g2JQT0=\n" +
                "-----END PRIVATE KEY-----";
        IDAdapter idAdapter = IDAdapterFactory.newInstance("139.198.126.227",2841,"88.708.8888/17301148449",300,privateKeyPem,1);
        IdentifierValue[] values = idAdapter.resolve("88.708.8888/06247219_1100194130");
    }

    @Test
    public void test7LoginNoPermissionResolve() throws Exception {
        String identifier = "88.300.15907541011/1024";
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
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37",5647,"88.300.15907541011/user002",300,privateKeyPem,1);
        IdentifierValue[] values = idAdapter.resolve(identifier);

        boolean hasIndex3 = false;
        for(IdentifierValue value:values){
            if(value.index==3){
                hasIndex3 = true;
            }
        }
        Assert.that(!hasIndex3,"没有权限的用户不可以查看管理员的值");
    }


}