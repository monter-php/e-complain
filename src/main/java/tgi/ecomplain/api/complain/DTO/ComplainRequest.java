package tgi.ecomplain.api.complain.DTO;

public record ComplainRequest(String message, String email, String firstName, String lastName, String ip) {
}
