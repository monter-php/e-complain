package tgi.ecomplain.domain.complain;

public class ComplainNotFoundException extends RuntimeException {
    public ComplainNotFoundException(String message) {
        super(message);
    }

    public ComplainNotFoundException(Long id) {
        super("Complain not found with id: " + id);
    }
}
