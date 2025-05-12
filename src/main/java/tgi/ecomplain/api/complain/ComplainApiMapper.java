package tgi.ecomplain.api.complain;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.api.complain.DTO.ComplainResponse;
import tgi.ecomplain.api.complain.DTO.UpdateComplainDTO;
import tgi.ecomplain.domain.complain.model.Complain;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplainApiMapper {

    @Mapping(target = "clientEmail", source = "email")
    @Mapping(target = "clientFirstName", source = "firstName")
    @Mapping(target = "clientLastName", source = "lastName")
    @Mapping(target = "country", ignore = true)
    UpdateComplainDTO toUpdateComplainDTO(ComplainRequest request);

    @Mapping(target = "complainId", source = "complainId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "counter", source = "counter")
    ComplainResponse toComplainResponse(Complain complain);

    List<ComplainResponse> toComplainResponseList(List<Complain> complains);
}
