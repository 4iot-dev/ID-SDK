package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class CertBuildTest {

    @Ignore
    @Test
    public void rootSelfSignCert() throws Exception {
        String rootPublicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnlek5Q99/6QQSsfezTSDhj1jlc66KnCK\n" +
                "B7LooKvrglfXijBbhU0wunhN44rGm/NNy344eZp63j8fo85YZAmfr8JxmBxqP7PGv78/AdBZay3U\n" +
                "drRV3Nxd++AFAyLI5JPVh7ssNovDzfKbX/cR+Qpku2MJ4XuES/UaathwEzCoiwbxdmHC2fSsK2oO\n" +
                "9LbwNa8+1CnMSgP43f3zgt7j4rbk/oEqrGlYKvMsZ7UkSlF6SLLWxEBx4EEB4/V8orM3CJXrQySB\n" +
                "Vxk1agQX1e909Q7ve5IUq5Ecje5s7kRVwE8kKJamZkxz1M3SMvackcE/Q46cRhOVHFrMq0MVA19u\n" +
                "13ZgJQIDAQAB\n" +
                "-----END PUBLIC KEY-----";

        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);

        System.out.println(rootPublicKey.toString());

        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/root-key.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);

        System.out.println(KeyConverter.toPkcs8UnencryptedPem(rootPrivateKey));

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.104.40", 2641);

        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, rootPublicKey);
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(null, Permission.EVERYTHING));
        values[1] = valueHelper.newCertValue(400, rootPublicKey,perms, "301:88.111.1/0.0", "301:88.111.1/0.0", rootPrivateKey, "2022-01-01 00:00:00", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.111.1/0.0", values);
    }
    @Ignore
    @Test
    public void shrSignCert() throws Exception {
        String issueRoot = "301:88.111.1/0.0";
        String rootPrivateKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/root-key.pem");

        PrivateKey rootPrivateKey = KeyConverter.fromPkcs8Pem(rootPrivateKeyPem, null);
        System.out.println(rootPrivateKey.toString());

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.104.40", 2641);
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
        values[1] = valueHelper.newCertValue(400, publicKey,perms, issueRoot, "301:88.111.1/88.111", rootPrivateKey, "2022-01-01 00:00:00", "2020-01-01 00:00:00", "2020-07-28 00:00:00");
        idAdapter.updateIdentifierValues("88.111.1/88.111", values);
    }

    @Ignore
    @Test
    public void lhsCertTest() throws Exception {
        String issue = "301:88.111.1/88.111";
        String issuePrivateKeyPem = "";
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

        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.104.40", 2641);
        ValueHelper valueHelper = ValueHelper.getInstance();

        IdentifierValue[] values = new IdentifierValue[2];
        values[0] = valueHelper.newPublicKeyValue(301, publicKey);
        values[1] = valueHelper.newCertValue(400, publicKey, issue, "301:88.111.1/88.167", issuePrivateKey, "2022-01-01 00:00:00", "2020-01-01 00:00:00", "2020-07-28 00:00:00");

        idAdapter.updateIdentifierValues("88.111.1/88.167", values);
    }
}
