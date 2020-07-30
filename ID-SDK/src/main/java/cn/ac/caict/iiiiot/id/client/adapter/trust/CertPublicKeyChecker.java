package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.adapter.IDAdapter;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;

import java.security.PublicKey;

public class CertPublicKeyChecker {

    public String checkPublicKeyIssue(JWS jws, IDAdapter idAdapter) {
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
