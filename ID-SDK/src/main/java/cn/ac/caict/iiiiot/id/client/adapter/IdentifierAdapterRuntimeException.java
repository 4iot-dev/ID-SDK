package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.BaseResponse;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;

public class IdentifierAdapterRuntimeException extends RuntimeException {
    private IdentifierException identifierException;

    public IdentifierAdapterRuntimeException() {
    }

    public IdentifierAdapterRuntimeException(String message) {
        super(message);
    }

    public IdentifierAdapterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentifierAdapterRuntimeException(Throwable cause) {
        super(cause);
    }

    public IdentifierAdapterRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdentifierAdapterRuntimeException(IdentifierException identifierException) {
        this.identifierException = identifierException;
    }

    public IdentifierAdapterRuntimeException(String message, IdentifierException identifierException) {
        super(message);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterRuntimeException(String message, Throwable cause, IdentifierException identifierException) {
        super(message, cause);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterRuntimeException(Throwable cause, IdentifierException identifierException) {
        super(cause);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, IdentifierException identifierException) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.identifierException = identifierException;
    }
}
