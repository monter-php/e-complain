package tgi.ecomplain.api.complain;

public record ComplainRequest(String message, String email, String firstName, String lastName, String ip) {
}
