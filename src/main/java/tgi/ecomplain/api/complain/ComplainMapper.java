package tgi.ecomplain.api.complain;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tgi.ecomplain.api.complain.DTO.UpdateComplainDTO;
import tgi.ecomplain.domain.complain.model.Complain;

@Mapper
public interface ComplainMapper {
    public final static ComplainMapper INSTANCE = Mappers.getMapper(ComplainMapper.class);

    @Mapping(target = "clientEmail", source = "email")
    @Mapping(target = "clientFirstName", source = "firstName")
    @Mapping(target = "clientLastName", source = "lastName")
    @Mapping(target = "country", ignore = true)
    UpdateComplainDTO toUpdateComplainDTO(ComplainRequest request);

    @Mapping(target = "complainId", source = "complainId")
    @Mapping(target = "status", expression = "java(complain.status().getValue())")
    @Mapping(target = "counter", source = "counter")
    ComplainResponse toComplainResponse(Complain complain);
}
