package tgi.ecomplain.domain.complain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.api.complain.DTO.PatchComplainRequest;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.geo.GeoIpData;
import tgi.ecomplain.domain.geo.GeoIpService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplainService {
    
    private final ComplainRepository complainRepository;
    private final ClientRepository clientRepository;
    private final GeoIpService geoIpService;
    
    public Complain createComplain(ComplainRequest request, String clientIp) {
        // Get or create client
        Client client = clientRepository.getClient(request.email());
        if (client == null) {
            client = clientRepository.createClient(
                request.email(),
                request.firstName(),
                request.lastName()
            );
        }

        Optional<Complain> existingComplain = complainRepository.findComplainByProductIdAndEmail(request.productId(), request.email());
        if (existingComplain.isPresent()) {
            log.info("Complain already exists for product {} and email {}", request.productId(), request.email());
            //increment counter
            Complain complain = existingComplain.get();
            complain.setCounter(complain.getCounter() + 1);
            return complainRepository.saveComplain(complain);
        }
            

        Optional<GeoIpData> geoIpResponse = geoIpService.getGeoIpData(clientIp);
        String country = geoIpResponse.map(GeoIpData::countryName).orElse("");
        
        // Create and save the complain
        Complain complain = Complain.builder()
            .productId(request.productId())
            .message(request.message())
            .creationDate(new Date())
            .client(client)
            .status(ComplainStatus.SUBMITTED.getValue())
            .country(country) // Using the client's IP address as the country for now
            .counter(1)  // Initial counter value
            .build();
        
        return complainRepository.saveComplain(complain);
    }

    public List<Complain> getComplainsByEmail(String email) {
        return complainRepository.findComplainsByEmail(email);
    }

    public Complain updateComplain(Long complainId, PatchComplainRequest patchRequest) {
        Complain existingComplain = complainRepository.findById(complainId)
                .orElseThrow(() -> new ComplainNotFoundException(complainId));

        Complain.ComplainBuilder updatedComplainBuilder = Complain.builder()
                .complainId(existingComplain.getComplainId())
                .creationDate(existingComplain.getCreationDate())
                .client(existingComplain.getClient())
                .counter(existingComplain.getCounter()); // Preserve original counter unless explicitly updatable

        // Apply updates from patchRequest
        if (patchRequest.getStatus() != null && !patchRequest.getStatus().isBlank()) {
            try {
                updatedComplainBuilder.status(patchRequest.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid status string, perhaps throw a specific validation exception
                // For now, rethrow or log. Consider a custom BadRequestBodyException.
                throw new IllegalArgumentException("Invalid status value: " + patchRequest.getStatus());
            }
        } else {
            updatedComplainBuilder.status(existingComplain.getStatus());
        }

        if (patchRequest.getMessage() != null) {
            updatedComplainBuilder.message(patchRequest.getMessage());
        } else {
            updatedComplainBuilder.message(existingComplain.getMessage());
        }

        if (patchRequest.getCountry() != null) {
            updatedComplainBuilder.country(patchRequest.getCountry());
        } else {
            updatedComplainBuilder.country(existingComplain.getCountry());
        }

        Complain updatedComplain = updatedComplainBuilder.build();
        return complainRepository.saveComplain(updatedComplain);
    }
}
