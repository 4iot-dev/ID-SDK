package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.adapter.trust.*;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.hutool.core.io.IoUtil;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 可信验证
 */
public class Verifier {

    private static Verifier verifier;

    private Verifier() {

    }

    public static Verifier getInstance() {
        if (verifier == null) {
            synchronized (Verifier.class) {
                if (verifier == null) {
                    verifier = new Verifier();
                }
            }
        }
        return verifier;
    }

    public VerifyResult verifyCert(String identifier,IdentifierValue jwsValue) throws IdentifierAdapterException, IdentifierTrustException {
        if (!Common.HS_CERT.equals((jwsValue.getTypeStr()))) {
            throw new IdentifierAdapterException("type must be HS_CERT");
        }
        String signatureString = jwsValue.getDataStr();
        JWS jws = JWSFactory.getInstance().deserialize(signatureString);

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
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

        List<PublicKey> rootKeys = getRootKeys();

        CertChainVerifier certChainVerifier = new CertChainVerifier(rootKeys);
        CertChainVerificationResult chainResult = certChainVerifier.verifyChain(issuedSignatures);
        chainResult.unableToBuildChain = unableToBuildChain;
        int code;
        if (chainResult.canTrust()) {
            code = 1;
            message = "Signature VERIFIED";
            String publicKeyIssue = new CertPublicKeyChecker().checkPublicKeyIssue(jws, idAdapter);
            if (publicKeyIssue != null) {
                message += "; WARNING " + publicKeyIssue;
            }
        } else {
            code = 0;
            message = "Signature NOT VERIFIED";
        }
        return new VerifyResult(code,message,chainResult);
    }

    private List<PublicKey> getRootKeys() throws IdentifierAdapterException {
        List<PublicKey> rootKeys = new ArrayList<>();
        InputStream in = Verifier.class.getResourceAsStream("/public_key.pem");
        try {
            String rootPublicKeyPem = IoUtil.read(in, "UTF-8");
            PublicKey rootPublicKey = KeyConverter.fromX509Pem(rootPublicKeyPem);
            rootKeys.add(rootPublicKey);
        } catch (Exception e) {
            throw new IdentifierAdapterException("load public key error", e);
        } finally {
            IoUtil.close(in);
        }
        return rootKeys;
    }

    public VerifyResult verifySignature(String identifier,IdentifierValue jwsValue, IdentifierValue[] values) throws IdentifierAdapterException, IdentifierTrustException {
        if (!Common.HS_SIGNATURE.equals(jwsValue.getTypeStr())) {
            throw new IdentifierAdapterException("type must be HS_SIGNATURE");
        }
        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();


        String signatureString = jwsValue.getDataStr();

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
                IdentifierValue[] identifierValues = idAdapter.resolve(issuerValRef.getIdentifierAsString(), null, new int[]{issuerValRef.index});
                IdentifierValue identifierValue = identifierValues[0];
                if (identifierValue != null) {
                    PublicKey issuerPublicKey = KeyConverter.fromX509Pem(identifierValue.getDataStr());
                    ValuesSignatureVerificationResult valuesReport = IdentifierVerifier.getInstance().verifyValues(identifier, ValueHelper.getInstance().filterOnlyPublicValues(Arrays.asList(values)), jws, issuerPublicKey);
                }
            } catch (Exception ex) {
                // ignore
            }
            message = "Signature NOT VERIFIED unable to build chain: " + e.getMessage();

        }

        List<PublicKey> rootKeys = getRootKeys();

        CertChainVerifier certChainVerifier = new CertChainVerifier(rootKeys);

        CertChainVerificationResult result = certChainVerifier.verifyValues(identifier, Arrays.asList(values), issuedSignatures);
        boolean badDigests = result.valuesResult.badDigestValues.size() != 0;
        boolean missingValues = result.valuesResult.missingValues.size() != 0;
        int code;
        if (result.canTrustAndAuthorized() && !badDigests && !missingValues) {
            code = 1;
            message = "Signature VERIFIED";

        } else {
            code = 0;
            message = "Signature NOT VERIFIED";
            if (badDigests) {
                message += " bad digests";
            }
            if (missingValues) {
                message += " missing values";
            }
        }

        return new VerifyResult(code,message,result);
    }

}
