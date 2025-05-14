package tgi.ecomplain.api.complain;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.api.complain.dto.ComplainResponse;
import tgi.ecomplain.domain.complain.model.Complain;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplainApiMapper {

    @Mapping(target = "complainId", source = "complainId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "counter", source = "counter")
    ComplainResponse toComplainResponse(Complain complain);

    List<ComplainResponse> toComplainResponseList(List<Complain> complains);
}
