package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.adapter.IDAdapterFactory;
import cn.ac.caict.iiiiot.id.client.adapter.ValueHelper;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CertTest {

    @Test
    public void rootSelfSignCert() throws Exception {
        String rootPublicKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_public_key.pem");

        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);

        System.out.println(rootPublicKey.toString());

        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_private_pkcs8.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);

        System.out.println(KeyConverter.toPkcs8UnencryptedPem(rootPrivateKey));

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance("192.168.150.37", 5643);

        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, rootPublicKey);
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(null, Permission.EVERYTHING));
        values[1] = valueHelper.newCertValue(400, rootPublicKey,perms, "301:88.300.15907541011/0.0", "301:88.300.15907541011/0.0", rootPrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

//        idAdapter.updateIdentifierValues("88.300.15907541011/0.0", values);
    }

//    @Test
    public void shrSignCert() throws Exception {
        String issueRoot = "301:88.300.15907541011/0.0";
        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_private_pkcs8.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);
        System.out.println(rootPrivateKey.toString());

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance("192.168.150.37", 5643);
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

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance("192.168.150.37", 5643);
        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, publicKey);
        values[1] = valueHelper.newCertValue(400, publicKey, issue, "301:88.300.15907541011/0.88.300.15907541011", issuePrivateKey, "2020-12-31 23:59:59", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.300.15907541011/0.88.300.15907541011", values);
    }

    @Test
    public void verifyCert() throws Exception {
        IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();

        String identifier = "88.300.15907541011/0.88.300.15907541011";
        IdentifierValue[] values = idAdapter.resolve(identifier, new String[]{"HS_CERT"}, null);
//        IdentifierValue[] hvs =  Util.filterValues(values,null, Common.HS_SIGNATURE_TYPE_LIST);
        String signatureString = values[0].getDataStr();
        System.out.println(signatureString);

        JWS jws = JWSFactory.getInstance().deserialize(signatureString);
        CertChainBuilder certChainBuilder = new CertChainBuilder(idAdapter);
        List<IssuedSignature> issuedSignatures = null;
        String message = "";
        boolean unableToBuildChain = false;
        try {
            issuedSignatures = certChainBuilder.buildChain(jws);
        } catch (IdentifierTrustException e) {
            message = "Signature NOT VERIFIED unable to build chain: " + e.getMessage();
            unableToBuildChain = true;
        }

        System.err.println(issuedSignatures);

        List<PublicKey> rootKeys = new ArrayList<>();
        String rootPublicKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_public_key.pem");
        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);
        rootKeys.add(rootPublicKey);

        CertChainVerifier certChainVerifier = new CertChainVerifier(rootKeys);

        CertChainVerificationResult chainReport = certChainVerifier.verifyChain(issuedSignatures);
        chainReport.unableToBuildChain = unableToBuildChain;
        String chainReportJson = GsonCompose.getPrettyGson().toJson(chainReport);
        System.out.println(chainReportJson);
        if (chainReport.canTrust()) {
            message = "Signature VERIFIED";
            String publicKeyIssue = new CertPublicKeyChecker().checkPublicKeyIssue(jws, idAdapter);
            if (publicKeyIssue != null) {
                message += "; WARNING " + publicKeyIssue;
            }
        } else {
            message = "Signature NOT VERIFIED";
        }
        System.out.println(message);
    }
}
