package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.adapter.IdentifierAdapterException;
import cn.ac.caict.iiiiot.id.client.adapter.IdentifierRecord;
import cn.ac.caict.iiiiot.id.client.adapter.cache.IdentifierRecordCache;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CertChainBuilder {
    private static final int MAX_CHAIN_LENGTH = 50;

    private Map<String, IdentifierRecord> handleMap;
    private IDAdapter idAdapter;

    private final JWSFactory signatureFactory = JWSFactory.getInstance();
    private final IdentifierVerifier identifierVerifier = new IdentifierVerifier();

    public CertChainBuilder(Map<String, IdentifierRecord> handleMap, IDAdapter idAdapter) {
        this.handleMap = handleMap;
        fixHandleMapCase();
        this.idAdapter = idAdapter;
    }

    public CertChainBuilder(IDAdapter idAdapter) {
        this.idAdapter = idAdapter;
    }


    private void fixHandleMapCase() {
        Map<String, IdentifierRecord> newEntries = null;
        for (Map.Entry<String, IdentifierRecord> entry : handleMap.entrySet()) {
            String key = entry.getKey();
            String upperCaseKey = Util.upperCasePrefix(key);
            if (!key.equals(upperCaseKey)) {
                if (newEntries == null) newEntries = new HashMap<>();
                newEntries.put(upperCaseKey, entry.getValue());
            }
        }
        if (newEntries != null) handleMap.putAll(newEntries);
    }

    public List<IssuedSignature> buildChain(JWS childSignature) throws IdentifierTrustException {
        List<IssuedSignature> result = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        List<String> chain = null;
        while (true) {
            IdentifierClaimsSet childClaims = identifierVerifier.getIdentifierClaimsSet(childSignature);
            if (childClaims == null) throw new IdentifierTrustException("signature payload not valid");
            if (childClaims.isSelfIssued()) {
                IssuedSignature issuedSignature = new IssuedSignature(childSignature, childClaims.publicKey, childClaims.perms);
                result.add(issuedSignature);
                break; //If we reach a self signed cert the chain is complete.
            }
            if (result.size() >= MAX_CHAIN_LENGTH) throw new IdentifierTrustException("chain too long");
            boolean noChain = false;
            if (chain == null || chain.isEmpty()) {
                chain = childClaims.chain;
                if (chain == null || chain.isEmpty()) {
                    noChain = true;
                    String identifierOfIssuer = ValueReference.transStr2ValueReference(childClaims.iss).getIdentifierAsString();
                    chain = Collections.singletonList(identifierOfIssuer);
                }
            }
            String nextLinkInChain = chain.get(0);
            if (seenIds.contains(nextLinkInChain)) {
                throw new IdentifierTrustException("cycle in chain");
            } else {
                seenIds.add(nextLinkInChain);
            }
            String parentSignatureString;
            try {
                parentSignatureString = lookup(nextLinkInChain, childClaims.iss);
            } catch (IdentifierAdapterException e) {
                throw new IdentifierTrustException("handle resolution exception", e);
            }
            if (parentSignatureString == null) {
                if (noChain)
                    throw new IdentifierTrustException("no chain and unable to resolve issuer " + nextLinkInChain);
                throw new IdentifierTrustException("unable to resolve chain " + nextLinkInChain);
            }
            JWS parentSignature;
            try {
                parentSignature = signatureFactory.deserialize(parentSignatureString);
            } catch (IdentifierTrustException e) {
                if (noChain)
                    throw new IdentifierTrustException("no chain and not a signature at issuer " + nextLinkInChain);
                throw new IdentifierTrustException("not a signature at chain " + nextLinkInChain);
            }
            IdentifierClaimsSet parentClaims = identifierVerifier.getIdentifierClaimsSet(parentSignature);
            if (parentClaims == null) throw new IdentifierTrustException("signature payload not valid");
            if (!Util.equalsPrefixCaseInsensitive(parentClaims.sub, childClaims.iss))
                throw new IdentifierTrustException("chain is broken");
            IssuedSignature issuedSignature = new IssuedSignature(childSignature, parentClaims.publicKey, parentClaims.perms);
            result.add(issuedSignature);
            childSignature = parentSignature;
            chain = chain.subList(1, chain.size()); //chain = tail of chain.
        }
        return result;
    }

    public IdentifierValue resolveValueReference(ValueReference valueReference) throws IdentifierAdapterException {
        if (handleMap != null) {
            return handleMapLookup(valueReference);
        }

        if (idAdapter != null) {
            IdentifierValue[] values = idAdapter.resolve(valueReference.getIdentifierAsString(), null, new int[]{valueReference.index});
            if (values.length == 1) {
                return values[0];
            }
        }
        return null;
    }

    private String lookup(String nextLinkInChain, String subject) throws IdentifierAdapterException, IdentifierTrustException {
        ValueReference valueReference = ValueReference.transStr2ValueReference(nextLinkInChain);
        if (valueReference.index > 0) {
            IdentifierValue value = resolveValueReference(valueReference);
            if (value == null) {
                return null;
            } else {
                return value.getDataStr();
            }
        } else {
            List<IdentifierValue> values = null;
            String identifier = valueReference.getIdentifierAsString();
            if (handleMap != null) {
                IdentifierRecord record = handleMap.get(Util.upperCasePrefix(identifier));
                if (record != null) values = record.getValues();
            }
            if (values == null) {
                IdentifierRecord identifierRecord = IdentifierRecordCache.getInstance().get(Util.upperCasePrefix(identifier));
                values = identifierRecord.getValues();
            }
            if (values == null) {
                if (idAdapter != null) {
                    IdentifierValue[] result = idAdapter.resolve(identifier, null, null);
                    IdentifierRecordCache.getInstance().cacheIdentifierRecord(new IdentifierRecord(Util.upperCasePrefix(identifier), result));
                    values = Arrays.asList(result);
                }
            }
            if (values == null) return null;
            JWS latestCert = getLatestHsCertAboutSubject(subject, values);
            if (latestCert == null) return null;
            return latestCert.serialize();
        }
    }


    private IdentifierValue handleMapLookup(ValueReference valueReference) {
        IdentifierRecord record = handleMap.get(Util.upperCasePrefix(valueReference.getIdentifierAsString()));
        if (record == null) return null;
        return record.getValueAtIndex(valueReference.index);
    }

    JWS getLatestHsCertAboutSubject(String subject, List<IdentifierValue> values) throws IdentifierTrustException {
        JWS latestCertAboutSubject = null;
        IdentifierClaimsSet latestCertClaimsSet = null;
        for (IdentifierValue value : values) {
            if (value.hasType(Common.HS_CERT_TYPE)) {
                String signatureString = value.getDataStr();
                JWS signature = signatureFactory.deserialize(signatureString);
                IdentifierClaimsSet claimsSet = identifierVerifier.getIdentifierClaimsSet(signature);
                if (subject.equals(claimsSet.sub)) {
                    if (latestCertAboutSubject == null) {
                        latestCertAboutSubject = signature;
                        latestCertClaimsSet = claimsSet;
                    } else if (issuedLater(claimsSet, latestCertClaimsSet)) {
                        latestCertAboutSubject = signature;
                        latestCertClaimsSet = claimsSet;
                    }
                }
            }
        }
        return latestCertAboutSubject;
    }

    private static boolean issuedLater(IdentifierClaimsSet claims1, IdentifierClaimsSet claims2) {
        if (claims1.iat == null) return false;
        if (claims2.iat == null) return true;
        return claims1.iat > claims2.iat;
    }
}