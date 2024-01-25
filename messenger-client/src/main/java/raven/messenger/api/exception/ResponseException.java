package raven.messenger.api.exception;

public class ResponseException extends Exception {

    public int getCode() {
        return code;
    }

    private final int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
