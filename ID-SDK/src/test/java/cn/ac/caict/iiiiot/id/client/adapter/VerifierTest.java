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

    @Ignore
    @Test
    public void rootSelfSignCert() throws Exception {
        String rootPublicKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_public_key.pem");

        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);

        System.out.println(rootPublicKey.toString());

        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_private_pkcs8.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);

        System.out.println(KeyConverter.toPkcs8UnencryptedPem(rootPrivateKey));

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);

        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, rootPublicKey);
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(null, Permission.EVERYTHING));
        values[1] = valueHelper.newCertValue(400, rootPublicKey,perms, "301:88.300.15907541011/0.0", "301:88.300.15907541011/0.0", rootPrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.300.15907541011/0.0", values);
    }
    @Ignore
    @Test
    public void shrSignCert() throws Exception {
        String issueRoot = "100:88";
        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_private_pkcs8.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);
        System.out.println(rootPrivateKey.toString());

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg8/iu5V74oY09H6IG5+ZpdrEtnoOKRlW\n" +
                "K9KsG6sd+2yIF34U0DoFI+Ojq/Lrcsd6NgXzhvjI124V/Zr2j8pJiOeu4BhI2Q4Uikj7FwBIItxv\n" +
                "zsXCcvr0f8zelwfSfULgYINqi25MzUylfKl2vBSqacyliNGiKNoN5BHTRZiOcuU8z/czfcXVzkDv\n" +
                "TPLWwAAHIi5jYXYmeBZMKEHVYQsqXEgQtnIwl8+p2scEtT125Iez0pahVFNWkpk3AK9TmIvAi8gf\n" +
                "p27bFxos3OM1boKuotvIPlTLv05Q24uwT5CfgV7vdZBJ29Gie3YAKyPaVHEMpUD/BgAg/v4kGRdZ\n" +
                "1Rs7MQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        PublicKey publicKey = KeyConverter.fromX509Pem(publicKeyPem);

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, publicKey);
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(null, Permission.EVERYTHING));
        values[1] = valueHelper.newCertValue(400, publicKey,perms, issueRoot, "301:88.300.15907541011/0.88.300", rootPrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.300.15907541011/0.88.300", values);
    }

    @Ignore
    @Test
    public void lhsCertTest() throws Exception {
        String issue = "301:88.300.15907541011/0.88.300";
        String issuePrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDz+K7lXvihjT0fogbn5ml2sS2\n" +
                "eg4pGVYr0qwbqx37bIgXfhTQOgUj46Or8utyx3o2BfOG+MjXbhX9mvaPykmI567gGEjZDhSKSPsX\n" +
                "AEgi3G/OxcJy+vR/zN6XB9J9QuBgg2qLbkzNTKV8qXa8FKppzKWI0aIo2g3kEdNFmI5y5TzP9zN9\n" +
                "xdXOQO9M8tbAAAciLmNhdiZ4FkwoQdVhCypcSBC2cjCXz6naxwS1PXbkh7PSlqFUU1aSmTcAr1OY\n" +
                "i8CLyB+nbtsXGizc4zVugq6i28g+VMu/TlDbi7BPkJ+BXu91kEnb0aJ7dgArI9pUcQylQP8GACD+\n" +
                "/iQZF1nVGzsxAgMBAAECggEAIhIeHTaqawcdsQTz0ZATexdtOW2bM2xlJbay3gQBH/gRxWDFqH0W\n" +
                "zwwMRmnoCicdo9Et/XlZELZX4NGpYcl78HNSdIJScih9jrEGlg9wlfI8xEnB1U6g3FfsQUW4VsBu\n" +
                "lwPhCmFcYMSrEeoL0tnUorxPHWpya/TRGl6xKZAhYfkXuP/NpGuzN7cQIDWp33P1ZXkCbqOw4iGC\n" +
                "hAjqdMfYaXPsrS7gml+irGswJvehfvF7WurrcBQCLs4XZAh+sc+TOAxBKBMBLsZMluFJA+W/MHi6\n" +
                "+9zHg6btNYz9bBrm3ltSGFR3XlTP4KBTc7qG74pHVpg7Qe1qQDpcfnfEg8mrAQKBgQDJw6cztmYp\n" +
                "e7J1N6kJY+4on915vfw46ZzXRaGI1mS5Vo6FquH7YUTOCYacdxUBr51yysjHnQrjr4/udF0zqNcH\n" +
                "gNtWGHWmPNlRgN+qfcTdAVvVDR5w+49vDu0rwSrmvQnWRL1LlMV+CJTMB9X6lFlHOeUKK7upLp6V\n" +
                "2uCqNIvCOQKBgQCnPn317Bswtr71h8BAA4uZk3J2jLcrbazUAhFr1A97s8BXdzqlYwdtb/Yq6lGt\n" +
                "Q56ZGR5VG1V4tTZI0XnMXIomwz2mQGNGWxPFlIFHCeglj3zBIb21XuFim67avUz0DEstkTN5TwYf\n" +
                "5LOQUj4tdMFoMcIWMAiufsIi7ySRG3rguQKBgDJUV/USXtGiRZXv2H67KsF/f2PK/IvF2pXojK7x\n" +
                "rBZ/fPXi9pQaY2tx/N4y5k6RXBkydHs2tWyucpzs8gLc7ya0AgKr/00EiMFIAIMq9Fyc5idzKlDM\n" +
                "r67obkkn2mfMaBPG+eFMrycNRPDQU5Q6RTr7OiMbXaiKNIz1GG3cxbAhAoGAEn5fkWUlcJKUtTPo\n" +
                "t2ts8XOTkbZnvt6m4N7FrGXLvIMY/tMJYiZ8OMbLst0sYPt8OzPC3ehi4DExqoW3cTi+ciDe3VZ7\n" +
                "Y9lIa23Lid53lEe0pOqlPrwaMjxC7p9GNipx2b0Xsw9g/v/cN1ZQ19f0VyBBdRCktR8BOrm1EnK2\n" +
                "eMkCgYB0Ab6vp6VC4+5DQSLv8Ze7BJmcdNU3bAQ//J9vP7U0m2g9KROw68OGUjxhDZNiFxsdMyzP\n" +
                "KzRtILLqRTEWrXXNG4wS7d2sAfRfxBLRY0NZcBFUM7/KPiogoKZQzckxZZCIzIKZ7BL5v2HNKFNj\n" +
                "qjUsZW3q9/v8rG834S9cLXS+FA==\n" +
                "-----END PRIVATE KEY-----";
        PrivateKey issuePrivateKey = KeyConverter.fromPkcs8Pem(issuePrivateKeyPem, null);

        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhKyKf1EFZZ1Qe4NW8pITZg+zWUtdoYJ6\n" +
                "OxpslmQ4acOzM0L+0Xo0R97XYmWLALAzdwcbjLPSY62HTbBBXeDGvYRUrcDou4g62GmAdVnMPZjp\n" +
                "SIRdA5cBH1mQnGXhEHhGw71dE3AaMajQKYrsgJXWapAkkN0prbanQanbfc4VRR1n3MjHzRfkMVK5\n" +
                "aatcOpE1p80B6iWI74KzRVsaUoVWeHYmycmq94c/LwtstUwui7oa+403Kxb1Dy35Kc05dbrqKEZ5\n" +
                "Qrb/fvAFOM5r+pHtug2P2PYJ91PNXXpJv+HXCOtUvvz0EfFidIbpbpzZs0xdEYX+BeQGmfLHu5vA\n" +
                "8KGQrwIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        PublicKey publicKey = KeyConverter.fromX509Pem(publicKeyPem);

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, publicKey);
        values[1] = valueHelper.newCertValue(400, publicKey, issue, "301:88.300.15907541011/0.88.300.15907541011", issuePrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.300.15907541011/0.88.300.15907541011", values);
    }

    @Test
    public void verifyCert() throws IdentifierAdapterException, IdentifierTrustException {
        IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();

        String identifier = "88.300.15907541011/0.88.300.15907541011";
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

        String identifier = "88.300.15907541011/1024";
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