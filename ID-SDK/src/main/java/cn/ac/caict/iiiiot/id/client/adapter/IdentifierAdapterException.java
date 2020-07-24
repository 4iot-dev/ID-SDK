package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;

public class IdentifierAdapterException extends Exception{

    private IdentifierException identifierException;

    public IdentifierAdapterException() {
    }

    public IdentifierAdapterException(String message) {
        super(message);
    }

    public IdentifierAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentifierAdapterException(Throwable cause) {
        super(cause);
    }

    public IdentifierAdapterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdentifierAdapterException(IdentifierException identifierException) {
        this.identifierException = identifierException;
    }

    public IdentifierAdapterException(String message, IdentifierException identifierException) {
        super(message);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterException(String message, Throwable cause, IdentifierException identifierException) {
        super(message, cause);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterException(Throwable cause, IdentifierException identifierException) {
        super(cause);
        this.identifierException = identifierException;
    }

    public IdentifierAdapterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, IdentifierException identifierException) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.identifierException = identifierException;
    }
}
