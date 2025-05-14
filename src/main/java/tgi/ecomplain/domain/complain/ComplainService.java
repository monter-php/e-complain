package tgi.ecomplain.domain.complain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgi.ecomplain.api.complain.dto.ComplainRequest;
import tgi.ecomplain.api.complain.dto.PatchComplainRequest;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.geo.GeoIpData;
import tgi.ecomplain.domain.geo.GeoIpService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplainService {

    private final ComplainRepository complainRepository;
    private final ClientRepository clientRepository;
    private final GeoIpService geoIpService;

    public Complain createComplain(ComplainRequest request, String clientIp) {

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
            log.warn("Complain already exists for product {} and email {}", request.productId(), request.email());

            Complain complain = existingComplain.get();
            complain.setCounter(complain.getCounter() + 1);

            return complainRepository.saveComplain(complain);
        }


        Optional<GeoIpData> geoIpResponse = geoIpService.getGeoIpData(clientIp);
        String country = geoIpResponse.map(GeoIpData::countryName).orElse("");

        Complain complain = Complain.builder()
                .productId(request.productId())
                .message(request.message())
                .creationDate(new Date())
                .client(client)
                .status(ComplainStatus.SUBMITTED.getValue())
                .country(country)
                .counter(1)
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
                .counter(existingComplain.getCounter());

        if (patchRequest.getStatus() != null && !patchRequest.getStatus().isBlank()) {
            try {
                updatedComplainBuilder.status(patchRequest.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
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

