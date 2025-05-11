package tgi.ecomplain.api.complain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tgi.ecomplain.domain.complain.ComplainService;
import tgi.ecomplain.domain.complain.model.Complain;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ComplainController {
    private final ComplainService complainService;
    private final ComplainMapper complainMapper = ComplainMapper.INSTANCE;

    @PostMapping("/complaints")
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
}
