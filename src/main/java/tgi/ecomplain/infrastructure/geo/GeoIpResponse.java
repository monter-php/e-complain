package tgi.ecomplain.infrastructure.geo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GeoIpResponse(String ip, String countryName, String regionName, String city, String zipCode,
                            Double latitude, Double longitude, String timeZone) {
}
