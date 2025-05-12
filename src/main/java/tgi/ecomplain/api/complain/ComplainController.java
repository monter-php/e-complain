package tgi.ecomplain.api.complain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.api.complain.DTO.ComplainResponse;
import tgi.ecomplain.api.complain.DTO.EmailRequest;
import tgi.ecomplain.api.complain.DTO.PatchComplainRequest;
import tgi.ecomplain.domain.complain.ComplainService;
import tgi.ecomplain.domain.complain.model.Complain;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/complains")
@RequiredArgsConstructor
@Tag(name = "Complain API", description = "API for managing complains")
public class ComplainController {
    private final ComplainService complainService;
    private final ComplainApiMapper complainMapper;

    @Operation(summary = "Create a new complain", responses = {
            @ApiResponse(responseCode = "201", description = "Complain created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComplainResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<ComplainResponse> createComplain(@RequestBody ComplainRequest request, HttpServletRequest httpServletRequest) {

        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

        Complain createdComplain = complainService.createComplain(request, clientIp);

        ComplainResponse response = complainMapper.toComplainResponse(createdComplain);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComplain.complainId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all complains for a given email via POST", description = "Retrieves a list of complains associated with the provided email in the request body.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of complains", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComplainResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload if email is missing or malformed")
    })
    @PostMapping("/by-email")
    public ResponseEntity<List<ComplainResponse>> getComplainsByEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body containing the email address", required = true, content = @Content(schema = @Schema(implementation = EmailRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody EmailRequest emailRequest) {
        List<Complain> complains = complainService.getComplainsByEmail(emailRequest.email());
        List<ComplainResponse> response = complainMapper.toComplainResponseList(complains);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an existing complain", responses = {
            @ApiResponse(responseCode = "200", description = "Complain updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComplainResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or invalid status value"),
            @ApiResponse(responseCode = "404", description = "Complain not found")
    })
    @PutMapping("/{complainId}")
    public ResponseEntity<ComplainResponse> updateComplain(
            @Parameter(description = "ID of the complain to be updated", required = true)
            @PathVariable Long complainId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Complain data to update. Only fields present will be updated.", required = true, content = @Content(schema = @Schema(implementation = PatchComplainRequest.class)))
            @RequestBody PatchComplainRequest patchRequest) {
        Complain updatedComplain = complainService.updateComplain(complainId, patchRequest);
        ComplainResponse response = complainMapper.toComplainResponse(updatedComplain);
        return ResponseEntity.ok(response);
    }
}
