package tgi.ecomplain.domain.geo;

import java.util.Optional;

public interface GeoIpService {
    Optional<GeoIpData> getGeoIpData(String ip);
}
