package tgi.ecomplain.infrastructure.geo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tgi.ecomplain.domain.geo.GeoIpData;
import tgi.ecomplain.domain.geo.GeoIpService;

import java.util.Optional;

@Slf4j
@Service
public class FreeGeoIpService implements GeoIpService {
    private static final String API_URL = "https://freegeoip.io/json/{ip}";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Optional<GeoIpData> getGeoIpData(String ip) {
        if (ip == null) {
            throw new NullPointerException("IP address cannot be null");
        }
        
        try {
            GeoIpResponse response = restTemplate.getForObject(API_URL, GeoIpResponse.class, ip);

            if (response == null) {
                log.warn("Failed to get geo ip data for ip {}", ip);
                return Optional.empty();
            }

            GeoIpData geoIpData = GeoIpDataMapper.INSTANCE.map(response);

            return Optional.ofNullable(geoIpData);
        } catch (RestClientException e) {
            log.error("Error while fetching geo IP data for ip {}: {}", ip, e.getMessage());
            return Optional.empty();
        }
    }
}
