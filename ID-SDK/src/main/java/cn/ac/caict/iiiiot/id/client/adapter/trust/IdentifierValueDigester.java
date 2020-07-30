package cn.ac.caict.iiiiot.id.client.adapter.trust;


import cn.ac.caict.iiiiot.id.client.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.caict.iiiiot.id.client.adapter.trust.DigestedIdentifierValues.DigestedIdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import org.apache.commons.codec.binary.Base64;

public class IdentifierValueDigester {
    private static final int VALUE_DIGEST_OFFSET = Common.FOUR_SIZE * 2;

    public DigestedIdentifierValues digest(List<IdentifierValue> values, String alg) throws NoSuchAlgorithmException {
        List<DigestedIdentifierValue> digests = new ArrayList<>();
        MessageDigest digester = MessageDigest.getInstance(alg);
        for (IdentifierValue value : values) {
            DigestedIdentifierValue digest = digest(value, digester);
            digests.add(digest);
        }
        DigestedIdentifierValues result = new DigestedIdentifierValues();
        result.alg = alg;
        result.digests = digests;
        return result;
    }

    private DigestedIdentifierValue digest(IdentifierValue value, MessageDigest digester) {
        byte[] digestBytes = digestHandleValue(value, digester);
        DigestedIdentifierValue result = new DigestedIdentifierValue();
        result.digest = Base64.encodeBase64String(digestBytes);
        result.index = value.getIndex();
        return result;
    }

    private byte[] digestHandleValue(IdentifierValue value, MessageDigest digester) {
        digester.reset();
        byte[] encodedHandleValue = MsgBytesConvertor.convertIdentifierValueToByte(value);
        digester.update(encodedHandleValue, VALUE_DIGEST_OFFSET, encodedHandleValue.length - VALUE_DIGEST_OFFSET);
        byte[] digestBytes = digester.digest();
        return digestBytes;
    }

    /**
     * Verifies that the given digests correspond to the given values.
     * Note that the function only verifies exact correspondence; see {@link IdentifierVerifier}
     * for methods that deal separately with undigested, digested-but-missing,
     * bad-digest, and verified values.
     *
     * @param digestedValues the digests to compare.
     * @param values the handle values to compare.
     * @return true if the digests and values correspond, otherwise false.
     * @throws NoSuchAlgorithmException
     */
    public boolean verify(DigestedIdentifierValues digestedValues, List<IdentifierValue> values) throws NoSuchAlgorithmException {
        Map<Integer, IdentifierValue> indexOfValues = new HashMap<>();
        for (IdentifierValue value : values) {
            indexOfValues.put(value.getIndex(), value);
        }

        if (indexOfValues.size() != digestedValues.digests.size()) {
            return false;
        }
        MessageDigest digester = MessageDigest.getInstance(digestedValues.alg);
        for (DigestedIdentifierValue digestedHandleValue : digestedValues.digests) {
            IdentifierValue value = indexOfValues.get(digestedHandleValue.index);
            if (value == null) {
                return false;
            }
            byte[] digestBytes = digestHandleValue(value, digester);
            String digestAsBase64 = Base64.encodeBase64String(digestBytes);
            if (!digestedHandleValue.digest.equals(digestAsBase64)) {
                return false;
            }
        }
        return true;
    }
}
