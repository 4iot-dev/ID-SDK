package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.EncryptionUtils;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import org.apache.commons.logging.Log;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Authentication {

    private Log log = IDLog.getLogger(Authentication.class);

    public boolean verifyIdentifierUser(String userIdIdentifier, PrivateKey privateKey) throws IdentifierAdapterException {
        ValueReference valueReference = ValueReference.transStr2ValueReference(userIdIdentifier);
        valueReference.getIdentifierAsString();

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
        IdentifierValue[] values = idAdapter.resolve(valueReference.getIdentifierAsString(), null, new int[]{valueReference.index});

        if (values.length < 1) {
            // 没有解析到用户
            return false;
        }

        IdentifierValue value = values[0];

        if(!Common.HS_PUBKEY.equals(value.getTypeStr())){
            // 不是pubbKey
            return false;
        }
        PublicKey publicKey ;
        try {
            publicKey = KeyConverter.fromX509Pem(value.getDataStr());
        } catch (Exception e) {
            log.warn("unable build publicKey",e);
            return false;
        }

        try {
            byte[] data = getPayload();
            String sign = EncryptionUtils.sign(data,privateKey);
            return EncryptionUtils.verify(data,publicKey,sign);
        } catch (Exception e) {
            log.warn("unable build publicKey",e);
        }

        return false;
    }

    protected byte[] getPayload(){
        return Util.encodeString("test");
    }
}
