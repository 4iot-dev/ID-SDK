package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;

public class IdentifierException extends Exception {
	private int excpCode;// 异常码

	public IdentifierException(int excpCode) {
		super();
		this.excpCode = excpCode;
	}

	public IdentifierException(int excpCode, String message) {
		super(message);
		this.excpCode = excpCode;
	}

	public IdentifierException(int excpCode, Throwable throwable) {
		super(throwable);
		this.excpCode = excpCode;
	}

	public IdentifierException(int excpCode, String message, Throwable throwable) {
		super(message, throwable);
		this.excpCode = excpCode;
	}
	
	public int getExceptionCode(){
		return excpCode;
	}

	public String toString() {
		String msg = getMessage();
		if (msg == null)
			msg = "";
		return "IdentifierException (" + getCodeDescription(excpCode) + ") " + msg;
	}

	public static final String getCodeDescription(int excpCode) {
		switch (excpCode) {
		case ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE:
			return "INVALID_VALUE";
		case ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR:
			return "INTERNAL_ERROR";
		case ExceptionCommon.EXCEPTIONCODE_FOUND_NO_SERVICE:
			return "SERVICE_NOT_FOUND";
		case ExceptionCommon.EXCEPTIONCODE_NO_ACCEPTABLE_IDCOMMUNICATIONITEMS:
			return "NO_ACCEPTABLE_INTERFACES";
		case ExceptionCommon.EXCEPTIONCODE_UNKNOWN_PROTOCOL:
			return "UNKNOWN_PROTOCOL";
		case ExceptionCommon.EXCEPTIONCODE_IDENTIFIER_ALREADY_EXISTS:
			return "IDENTIFIER_ALREADY_EXISTS";
		case ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR:
			return "MESSAGE_FORMAT_ERROR";
		case ExceptionCommon.EXCEPTIONCODE_CANNOT_CONNECT_TO_ID_SYS_SERVER:
			return "CANNOT_CONNECT_TO_SERVER";
		case ExceptionCommon.EXCEPTIONCODE_UNABLE_TO_AUTHENTICATE:
			return "UNABLE_TO_AUTHENTICATE";
		case ExceptionCommon.EXCEPTIONCODE_IDENTIFIER_DOES_NOT_EXIST:
			return "IDENTIFIER_DOES_NOT_EXIST";
		case ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT:
			return "SECURITY_ALERT";
		case ExceptionCommon.EXCEPTIONCODE_UNKNOWN_ALGORITHM_ID:
			return "UNKNOWN_ALGORITHM_ID";
		case ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE:
			return "MISSING_OR_INVALID_SIGNATURE";
		case ExceptionCommon.EXCEPTIONCODE_MISSING_CRYPTO_PROVIDER:
			return "MISSING_CRYPTO_PROVIDER";
		case ExceptionCommon.EXCEPTIONCODE_ID_SYS_SERVER_ERROR:
			return "SERVER_ERROR";
		case ExceptionCommon.EXCEPTIONCODE_GOT_EXPIRED_MESSAGE:
			return "GOT_EXPIRED_MESSAGE";
		case ExceptionCommon.EXCEPTIONCODE_ENCRYPTION_ERROR:
			return "ENCRYPTION_ERROR";
		case ExceptionCommon.EXCEPTIONCODE_LONGCONNSOCKET_CREATE_FAILED:
			return "LONGCONNSOCKET_CREATE_FAILED";
		case ExceptionCommon.EXCEPTIONCODE_SERVICE_REFERRAL_ERROR:
			return "SERVICE_REFERRAL_ERROR";
		case ExceptionCommon.EXCEPTIONCODE_UNSUPPORTENCODING:
			return "SDK_INNER_UNSUPPORTENCODING_EXCEPTION";
		case ExceptionCommon.EXCEPTIONCODE_DISCONN_FAILED:
			return "DISCONNECT_FAILED";
		case ExceptionCommon.EXCEPTIONCODE_ILLEGAL_IP:
			return "ILLEGAL_IP:";
		case ExceptionCommon.EXCEPTIONCODE_ILLEGAL_PORT:
			return "ILLEGAL_PORT";
		case ExceptionCommon.INVALID_PARM:
			return "INVALID_PARAMETER";
		case ExceptionCommon.IDENTIFIER_ENGINE_ERROR:
			return "IDENTIFIER_ENGINE_ERROR";
		case ExceptionCommon.TIME_PARSE_ERROR:
			return "TIME_PARSE_ERROR";
		case ExceptionCommon.UNKNOWN_HOSTNAME_ERROR:
			return "UNKNOWN_HOSTNAME_ERROR";
		default:
			return "UNKNOWN_ERROR(" + excpCode + ")";
		}
	}
}