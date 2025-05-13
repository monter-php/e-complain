package tgi.ecomplain.api.complain.DTO;

import jakarta.validation.constraints.NotBlank;

public record ComplainRequest(
        @NotBlank String productId,
        @NotBlank String message,
        @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String ip) {
}
