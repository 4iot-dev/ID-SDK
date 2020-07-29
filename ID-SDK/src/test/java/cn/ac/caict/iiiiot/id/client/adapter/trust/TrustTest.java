package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.adapter.IDAdapterFactory;
import cn.ac.caict.iiiiot.id.client.adapter.IdentifierAdapterException;
import cn.ac.caict.iiiiot.id.client.adapter.ValueHelper;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Test;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrustTest {
    @Test
    public void certTrust() throws IdentifierAdapterException {
        IDAdapter idAdapter = IDAdapterFactory.newInstance("192.168.150.37", 5643);
        String[] types = new String[]{"HS_PUBKEY", "HS_CERT"};
        IdentifierValue[] values = idAdapter.resolve("88.300.15907541011/0.88.300.15907541011", types, null);
        for (int i = 0; i < values.length; i++) {
            System.out.println(values[i].toString());
        }
    }

    @Test
    public void t0verifyIdentifier() throws Exception {
        IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
        IDAdapter idAdapter = IDAdapterFactory.newInstance();

        String identifier = "0.NA/86.111";
        IdentifierValue[] values = idAdapter.resolve("0.NA/86.111", null, null);
//        IdentifierValue[] hvs =  Util.filterValues(values,null, Common.HS_SIGNATURE_TYPE_LIST);
        String signatureString = values[0].getDataStr();


        JsonWebSignature jws = JsonWebSignatureFactory.getInstance().deserialize(signatureString);
        ChainBuilder chainBuilder = new ChainBuilder(idAdapter);
        List<IssuedSignature> issuedSignatures = null;
        String message = "";
        try {
            issuedSignatures = chainBuilder.buildChain(jws);
        } catch (TrustException e) {
            try {
                IdentifierClaimsSet claims = IdentifierVerifier.getInstance().getIdentifierClaimsSet(jws);
                String issuer = claims.iss;
                ValueReference issuerValRef = ValueReference.transStr2ValueReference(issuer);
                IdentifierValue[] handleValues = idAdapter.resolve(issuerValRef.getIdentifierAsString(), null, new int[]{issuerValRef.index});
                IdentifierValue identifierValue = handleValues[0];
                if (identifierValue != null) {
                    PublicKey issuerPublicKey = Util.getPublicKeyFromBytes(identifierValue.getData());
                    ValuesSignatureVerificationReport valuesReport = IdentifierVerifier.getInstance().verifyValues(identifier, ValueHelper.getInstance().filterOnlyPublicValues(Arrays.asList(values)), jws, issuerPublicKey);
                    String valuesReportJson = GsonUtility.getPrettyGson().toJson(valuesReport);
                    System.out.println(valuesReportJson);
                }
            } catch (Exception ex) {
                // ignore
            }
            message = "Signature NOT VERIFIED unable to build chain: " + e.getMessage();
            System.out.println(message);

        }

        ChainVerifier chainVerifier = new ChainVerifier(new ArrayList<>());

        ChainVerificationReport report = chainVerifier.verifyValues(identifier, Arrays.asList(values), issuedSignatures);
        String reportJson = GsonUtility.getPrettyGson().toJson(report);
        System.out.println(reportJson);
        boolean badDigests = report.valuesReport.badDigestValues.size() != 0;
        boolean missingValues = report.valuesReport.missingValues.size() != 0;
        if (report.canTrustAndAuthorized() && !badDigests && !missingValues) {
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


    @Test
    public void t1verifyCert() throws Exception {
        IdentifierVerifier identifierVerifier = IdentifierVerifier.getInstance();
        IDAdapter idAdapter = IDAdapterFactory.newInstance();

        String identifier = "88.300.15907541011/0.88.300.15907541011";
        IdentifierValue[] values = idAdapter.resolve(identifier, new String[]{"HS_CERT"}, null);
//        IdentifierValue[] hvs =  Util.filterValues(values,null, Common.HS_SIGNATURE_TYPE_LIST);
        String signatureString = values[0].getDataStr();
        System.out.println(signatureString);

        JsonWebSignature jws = JsonWebSignatureFactory.getInstance().deserialize(signatureString);
        ChainBuilder chainBuilder = new ChainBuilder(idAdapter);
        List<IssuedSignature> issuedSignatures = null;
        String message = "";
        boolean unableToBuildChain = false;
        try {
            issuedSignatures = chainBuilder.buildChain(jws);
        } catch (TrustException e) {
            message = "Signature NOT VERIFIED unable to build chain: " + e.getMessage();
            unableToBuildChain = true;
        }

        System.err.println(issuedSignatures);

        List<PublicKey> rootKeys = new ArrayList<>();
        String rootPublicKeyPem = ResourceUtil.readUtf8Str("/Users/bluepoint/temp/ote-root-cert/rsa_public_key.pem");
        PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);
        rootKeys.add(rootPublicKey);

        ChainVerifier chainVerifier = new ChainVerifier(rootKeys);

        ChainVerificationReport chainReport = chainVerifier.verifyChain(issuedSignatures);
        chainReport.unableToBuildChain = unableToBuildChain;
        String chainReportJson = GsonUtility.getPrettyGson().toJson(chainReport);
        System.out.println(chainReportJson);
        if (chainReport.canTrust()) {
            message = "Signature VERIFIED";
            String publicKeyIssue = checkPublicKeyIssue(jws, idAdapter);
            if (publicKeyIssue != null) {
                message += "; WARNING " + publicKeyIssue;
            }
        } else {
            message = "Signature NOT VERIFIED";
        }
        System.out.println(message);


    }

    private String checkPublicKeyIssue(JsonWebSignature jws, IDAdapter idAdapter) {
        try {
            IdentifierClaimsSet claims = IdentifierVerifier.getInstance().getIdentifierClaimsSet(jws);
            PublicKey pubKeyInCert = claims.publicKey;
//            byte[] certPubKeyBytes = ValueHelper.getInstance().getBytesFromPublicKey(pubKeyInCert);
            String certPubKeyPem = KeyConverter.toX509Pem(pubKeyInCert);
            ValueReference valRef = ValueReference.transStr2ValueReference(claims.sub);
            IdentifierValue[] values;
            if (valRef.index == 0) {
                values = idAdapter.resolve(valRef.getIdentifierAsString(), new String[]{"HS_PUBKEY"}, null);
            } else {
                values = idAdapter.resolve(valRef.getIdentifierAsString(), null, new int[]{valRef.index});
            }
            for (IdentifierValue value : values) {

                if(certPubKeyPem.equals(value.getDataStr())){
                    return null;
                }

            }
            return "publicKey does not match subject";
        } catch (Exception e) {
            e.printStackTrace();
            return "exception checking publicKey: " + e.getMessage();
        }
    }
}
