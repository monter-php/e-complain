package tgi.ecomplain.infrastructure.geo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tgi.ecomplain.domain.geo.GeoIpData;
import tgi.ecomplain.domain.geo.GeoIpService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPAPIGeoIpService implements GeoIpService {
    private static final String API_URL = "http://ip-api.com/json/{ip}?fields=country,query";

    private final RestTemplate restTemplate;
    private final GeoIpDataMapper geoIpDataMapper;

    @Override
    public Optional<GeoIpData> getGeoIpData(String ip) {
        if (ip == null) {
            throw new IllegalArgumentException("IP address cannot be null");
        }
        
        try {
            GeoIpResponse response = restTemplate.getForObject(API_URL, GeoIpResponse.class, ip);

            if (response == null || response.country() == null) {
                log.warn("Failed to get geo ip data for ip {}", ip);
                return Optional.empty();
            }

            GeoIpData geoIpData = geoIpDataMapper.map(response);
            
            if (geoIpData == null) {
                log.warn("Failed to map geo ip data for ip {}", ip);
                return Optional.empty();
            }

            return Optional.of(geoIpData);
        } catch (RestClientException e) {
            log.error("Error while fetching geo IP data for ip {}: {}", ip, e.getMessage());
            return Optional.empty();
        }
    }
}
