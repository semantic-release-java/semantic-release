package gg.nils.semanticrelease.api.error;

public class SemanticReleaseException extends RuntimeException {

    public SemanticReleaseException(String message) {
        super(message);
    }

    public SemanticReleaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
