package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import org.junit.Test;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class IDUserTest {

    @Test
    public void test() throws Exception {
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);

        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newAdminValue(100,"88.300.15907541011/user002",300);

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
        idAdapter.createIdentifier("88.300.15907541011/user002", values);

    }

    @Test
    public void testModUser() throws Exception {
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

}