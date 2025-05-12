package tgi.ecomplain.infrastructure.geo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.domain.geo.GeoIpData;

@Mapper(componentModel = "spring")
public interface GeoIpDataMapper {

    @Mapping(target = "countryName", source = "country")
    @Mapping(target = "ip", source = "query")
    GeoIpData map(GeoIpResponse response);
}
