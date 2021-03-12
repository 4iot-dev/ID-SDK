package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.adapter.trust.GsonCompose;
import cn.ac.caict.iiiiot.id.client.adapter.trust.IdentifierTrustException;
import cn.ac.caict.iiiiot.id.client.adapter.trust.IdentifierVerifier;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class VerifierTest {

    @Test
    public void verifyCert() throws IdentifierAdapterException, IdentifierTrustException {
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();

        String identifier = "88.111.1/88.167";
        IdentifierValue[] values = idAdapter.resolve(identifier, new String[]{"HS_CERT"}, null);
        IdentifierValue certValue = values[0];

        Verifier verifier = Verifier.getInstance();
        VerifyResult result = verifier.verifyCert(identifier,certValue);
        String json = GsonCompose.getPrettyGson().toJson(result);
        System.out.println(json);
        Assert.assertEquals(result.getCode(),1);
    }

    @Test
    public void test1InitValue() throws Exception {
        String issue = "301:88.300.15907541011/0.88.300.15907541011";
        String issuePrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCErIp/UQVlnVB7g1bykhNmD7NZ\n" +
                "S12hgno7GmyWZDhpw7MzQv7RejRH3tdiZYsAsDN3BxuMs9JjrYdNsEFd4Ma9hFStwOi7iDrYaYB1\n" +
                "Wcw9mOlIhF0DlwEfWZCcZeEQeEbDvV0TcBoxqNApiuyAldZqkCSQ3SmttqdBqdt9zhVFHWfcyMfN\n" +
                "F+QxUrlpq1w6kTWnzQHqJYjvgrNFWxpShVZ4dibJyar3hz8vC2y1TC6Luhr7jTcrFvUPLfkpzTl1\n" +
                "uuooRnlCtv9+8AU4zmv6ke26DY/Y9gn3U81dekm/4dcI61S+/PQR8WJ0hulunNmzTF0Rhf4F5AaZ\n" +
                "8se7m8DwoZCvAgMBAAECggEAVwy+cnq+h8WIIQIz8kRV5gk3K8d/T5FEkbLsvGBVg0+cLG4ws1Dz\n" +
                "LJTRzQXDBliV6+sF8HOr/yrEM/0JZocs3vwMc1S3XxQrSAMw/c5Fa8UAi0wNm2Vgnyks8PEHkr7X\n" +
                "72Y3w6EyDpMswZUdR+EIJjMdSs9RoWDOrQ69rPWijw2iUtQNp71pm1dKM9ci60zEJ5GkCNCDl+pW\n" +
                "UPxguqSAotpLYs717Sx2xMqAWu6vqPgLBYvNxskUzGJwNWcz9/gKC9bES/mcwL57xq/la2O9FHZD\n" +
                "StT9yfrA1vRvK2Y95GDsiNLd1mqNuDmbmgzb7qnWBkwlIroMqnZAhnhJnv2qQQKBgQC6zq4Wv6JG\n" +
                "SmWTWD/CiwatsM2+i2jbt/sjyrm4xqkw/lUH77OBdS8h9I65h1OYWKM7fkX6khR5n2IGTqxLZXNY\n" +
                "8ZFo8etlTurZjA+eKUq4/XMPo9EP1Q5igcFPYm6HRoHnBToRpx/eGW8BJYwkfIj7ShQNHQc1XXMH\n" +
                "P0/hXZluLQKBgQC10N1L3kof38be2H86/KcQQAYZoiarjCEHMh/rDc4sAYSgLc1uEZg+2KbeG4t0\n" +
                "q+xRSWsg3OLiL2iFmTcRmVQtODn+DsulRVaWYM35C0g0EZl0V3uDZ1QR7UQQCWcQQX15erSaomXa\n" +
                "c1cKCCVLjeNP9yfDkxbtT1ehpGTs0bbfywKBgQCLS/TKp8k0iuNlVOfuhEbNm8o/rKrbNDzD+rY0\n" +
                "j4a4Lt5vFZGQFja4JimLNNrj/ixx/33lE6rK6ktpEp8tdY/mofNhAwwZthgvcl5u/LFoci2rWudY\n" +
                "2/FnCQSB0o9CthPFlcMwSKPdtK9udnYi+u4WG64J/TxT0cQlglNpZKc1XQKBgG1ZiT8wGnTBKeKt\n" +
                "YxgfDHuBhim0lH1ochTZ2MiOLQnaf+G2qm8gtehWdUXwWxd4r1DUT1Ich3hx+vMfCzfSeAYaYwah\n" +
                "72kfIsyevEKkvnBShXCrjOWjJ7UAdocOoKXuPYDqg+Tc91VDOX4XGBcw5x4ZEBlYRdnEdngJX4nv\n" +
                "N4WxAoGBAJnk6d/VLMJSvnzDrtLo4hQUzrwnqEuVYE5CkI7IQxl59Kvoi25AuNhUwzDmiI3YrZ36\n" +
                "kCG6wLQoL8AoJPYSXdKFVJC2e6nW5Q5HbSEl2KPS2kXWhTwUXff/Zc4WgNi0BtIjkCCLdqtW6xxs\n" +
                "NSKzeLnSePMDCfeGs791TrwlgGJE\n" +
                "-----END PRIVATE KEY-----";
        PrivateKey issuePrivateKey = KeyConverter.fromPkcs8Pem(issuePrivateKeyPem, null);

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        String identifier = "88.300.15907541011/1024";
        List<IdentifierValue> values = new ArrayList<>();

        values.add(new IdentifierValue(1, "URL", "https://www.citln.cn/"));
        values.add(new IdentifierValue(2, "EMAIL", "test@email.com"));

        IdentifierValue signValue = valueHelper.newSignatureValue(401, valueHelper.listToArray(values), issue, identifier, issuePrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00", "SHA-256");
        values.add(signValue);

        idAdapter.deleteIdentifier(identifier);
        idAdapter.createIdentifier(identifier, valueHelper.listToArray(values));
    }

    @Test
    public void test2VerifySignature() throws IdentifierAdapterException, IdentifierTrustException {
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();

        String identifier = "88.300.15907541011/1038";
        IdentifierValue[] values = idAdapter.resolve(identifier, null, null);

        //过滤签名数据
        List<IdentifierValue> list = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            if (values[i].getTypeStr().equals(Common.HS_SIGNATURE)) {
                list.add(values[i]);
            }
        }
        IdentifierValue signValue = list.get(0);

        //验证第一个签名
        Verifier verifier = Verifier.getInstance();
        VerifyResult result = verifier.verifySignature(identifier,signValue, values);
        String json = GsonCompose.getPrettyGson().toJson(result);
        System.out.println(json);

        Assert.assertEquals(result.getCode(),1);
    }
}