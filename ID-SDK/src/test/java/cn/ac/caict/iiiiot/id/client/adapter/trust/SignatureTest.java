package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.adapter.IDAdapterFactory;
import cn.ac.caict.iiiiot.id.client.adapter.ValueHelper;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignatureTest {

    @Test
    public void initValue() throws Exception {
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

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        String identifier = "88.300.15907541011/1";

        IdentifierValue[] values = new IdentifierValue[1];
        values[0] = new IdentifierValue(1,"URL","http://www.baidu.com");


        IdentifierValue[] valuesa = new IdentifierValue[2];
        valuesa[0] = values[0];
        valuesa[1] = valueHelper.newSignatureValue(401,values,issue,identifier,issuePrivateKey,"2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00","SHA-256");


        idAdapter.createIdentifier(identifier,valuesa);
    }

    @Test
    public void verifyIdentifier() throws Exception {
        IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();

        String identifier = "88.300.15907541011/1";
        IdentifierValue[] values = idAdapter.resolve(identifier, null, null);

        List<IdentifierValue> list = new ArrayList<>(values.length);
        for(int i=0;i<values.length;i++){
            if(values[i].getTypeStr().equals(Common.HS_SIGNATURE)){
                list.add(values[i]);
            }
        }

        String signatureString = list.get(0).getDataStr();


        JWS jws = JWSFactory.getInstance().deserialize(signatureString);
        CertChainBuilder certChainBuilder = new CertChainBuilder(idAdapter);
        List<IssuedSignature> issuedSignatures = null;
        String message = "";
        try {
            issuedSignatures = certChainBuilder.buildChain(jws);
        } catch (IdentifierTrustException e) {
            try {
                IdentifierClaimsSet claims = IdentifierVerifier.getInstance().getIdentifierClaimsSet(jws);
                String issuer = claims.iss;
                ValueReference issuerValRef = ValueReference.transStr2ValueReference(issuer);
                IdentifierValue[] handleValues = idAdapter.resolve(issuerValRef.getIdentifierAsString(), null, new int[]{issuerValRef.index});
                IdentifierValue identifierValue = handleValues[0];
                if (identifierValue != null) {
                    PublicKey issuerPublicKey = KeyConverter.fromX509Pem(identifierValue.getDataStr());
                    ValuesSignatureVerificationResult valuesReport = IdentifierVerifier.getInstance().verifyValues(identifier, ValueHelper.getInstance().filterOnlyPublicValues(Arrays.asList(values)), jws, issuerPublicKey);
                    String valuesReportJson = GsonCompose.getPrettyGson().toJson(valuesReport);
                    System.out.println(valuesReportJson);
                }
            } catch (Exception ex) {
                // ignore
            }
            message = "Signature NOT VERIFIED unable to build chain: " + e.getMessage();
            System.out.println(message);

        }

        List<PublicKey> rootKeys = new ArrayList<>();
        String rootPublicKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_public_key.pem");
        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);
        rootKeys.add(rootPublicKey);

        CertChainVerifier certChainVerifier = new CertChainVerifier(rootKeys);

        CertChainVerificationResult result = certChainVerifier.verifyValues(identifier, Arrays.asList(values), issuedSignatures);
        String reportJson = GsonCompose.getPrettyGson().toJson(result);
        System.out.println(reportJson);
        boolean badDigests = result.valuesResult.badDigestValues.size() != 0;
        boolean missingValues = result.valuesResult.missingValues.size() != 0;
        if (result.canTrustAndAuthorized() && !badDigests && !missingValues) {
            message = "Signature VERIFIED";
        } else {
            message = "Signature NOT VERIFIED";
            if (badDigests) {
                message += " bad digests";
            }
            if (missingValues) {
                message += " missing values";
            }
        }

        System.out.println(message);
    }

}
