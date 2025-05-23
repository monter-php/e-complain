package tgi.ecomplain.api.complain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tgi.ecomplain.api.complain.dto.*;
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
    public ResponseEntity<ComplainResponse> createComplain(@Valid @RequestBody ComplainRequest complainRequest, HttpServletRequest httpServletRequest) {

        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

        Complain createdComplain = complainService.createComplain(complainRequest, clientIp);
        ComplainResponse response = complainMapper.toComplainResponse(createdComplain);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComplain.getComplainId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all complains for a given email via POST", description = "Retrieves a list of complains associated with the provided email in the request body.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of complains", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComplainResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload if email is missing or malformed")
    })
    @PostMapping("/by-email")
    public ResponseEntity<List<ComplainResponse>> getComplainsByEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body containing the email address", required = true, content = @Content(schema = @Schema(implementation = SearchByEmailRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody SearchByEmailRequest searchByEmailRequest) {
        List<Complain> complains = complainService.getComplainsByEmail(searchByEmailRequest.email());
        List<ComplainResponse> response = complainMapper.toComplainResponseList(complains);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get complain details by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved complain details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComplainDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Complain not found")
    })
    @GetMapping("/{complainId}")
    public ResponseEntity<ComplainDetailResponse> getComplainById(
            @Parameter(description = "ID of the complain to retrieve", required = true)
            @PathVariable Long complainId) {
        Complain updatedComplain = complainService.getComplainById(complainId);
        ComplainDetailResponse response = complainMapper.toComplainDetailsResponse(updatedComplain); // And here
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
            @Valid @RequestBody PatchComplainRequest patchRequest) {
        Complain updatedComplain = complainService.updateComplain(complainId, patchRequest);
        ComplainResponse response = complainMapper.toComplainResponse(updatedComplain);
        return ResponseEntity.ok(response);
    }
}
