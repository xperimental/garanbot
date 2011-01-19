package net.sourcewalker.garanbot.api;

public class ClientException extends Exception {

    private static final long serialVersionUID = -2288239938972004990L;

    public ClientException() {
        super();
    }

    public ClientException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ClientException(String detailMessage) {
        super(detailMessage);
    }

    public ClientException(Throwable throwable) {
        super(throwable);
    }

}
