package tgi.ecomplain.domain.complain.exception;

public class ComplainNotFoundException extends RuntimeException {
    public ComplainNotFoundException(String message) {
        super(message);
    }
}
