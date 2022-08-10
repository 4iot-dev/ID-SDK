package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;

public class IdentifierTrustException extends IdentifierException {

    public IdentifierTrustException(String message) {
        super(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, message);
    }

    public IdentifierTrustException(String message, Throwable cause) {
        super(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, message, cause);
    }

}