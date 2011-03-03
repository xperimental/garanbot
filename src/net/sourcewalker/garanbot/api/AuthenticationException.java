package net.sourcewalker.garanbot.api;

/**
 * Specialized exception which signals a authentication error.
 * 
 * @author Xperimental
 */
public class AuthenticationException extends ClientException {

    private static final long serialVersionUID = 2991673478702964837L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AuthenticationException(String detailMessage) {
        super(detailMessage);
    }

    public AuthenticationException(Throwable throwable) {
        super(throwable);
    }

}
