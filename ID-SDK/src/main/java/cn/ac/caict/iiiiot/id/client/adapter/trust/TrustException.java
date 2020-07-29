package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;

public class TrustException extends IdentifierException {

    public TrustException(String message) {
        super(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, message);
    }

    public TrustException(String message, Throwable cause) {
        super(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, message, cause);
    }

}