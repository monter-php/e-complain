package tgi.ecomplain.infrastructure.geo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tgi.ecomplain.domain.geo.GeoIpData;

@Mapper
public interface GeoIpDataMapper {

    GeoIpDataMapper INSTANCE = Mappers.getMapper(GeoIpDataMapper.class);

    GeoIpData map(GeoIpResponse response);
}
