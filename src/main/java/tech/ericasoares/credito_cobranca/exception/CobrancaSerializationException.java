package tech.ericasoares.credito_cobranca.exception;

public class CobrancaSerializationException extends RuntimeException {

    public CobrancaSerializationException(String message) {
        super(message);
    }

    public CobrancaSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

