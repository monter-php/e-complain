package tgi.ecomplain.domain.complain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tgi.ecomplain.api.complain.ComplainRequest;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.geo.GeoIpData;
import tgi.ecomplain.domain.geo.GeoIpService;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

        Optional<GeoIpData> geoIpResponse = geoIpService.getGeoIpData(clientIp);
        String country = geoIpResponse.map(GeoIpData::countryName).orElse(null);
        
        // Create and save the complain
        Complain complain = Complain.builder()
            .message(request.message())
            .creationDate(new Date())
            .client(client)
            .country(country) // Using the client's IP address as the country for now
            .counter(1)  // Initial counter value
            .build();
        
        return complainRepository.saveComplain(complain);
    }
}
