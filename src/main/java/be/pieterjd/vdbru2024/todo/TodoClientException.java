package be.pieterjd.vdbru2024.todo;

public class TodoClientException extends RuntimeException {
    public TodoClientException() {
        super();
    }

    public TodoClientException(String message) {
        super(message);
    }

    public TodoClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoClientException(Throwable cause) {
        super(cause);
    }

    protected TodoClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
