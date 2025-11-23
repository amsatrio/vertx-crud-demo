package io.github.amsatrio.vertx_crud_demo.dto.exception;

public class DataExistException extends RuntimeException {
    static final long serialVersionUID = -3387516993124229948L;

    public DataExistException() {
    }

    public DataExistException(String message) {
        super(message);
    }

    public DataExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataExistException(Throwable cause) {
        super(cause);
    }

    protected DataExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}