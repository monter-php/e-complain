package tgi.ecomplain.api.complain.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmailRequest(
        @Schema(description = "Email address of the client", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email
) {
}
