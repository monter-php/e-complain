package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tgi.ecomplain.api.complain.ComplainRequest;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.infrastructure.complain.ComplainEntity;

@Mapper(uses = {ClientMapper.class})
public interface ComplainMapper {
    
    ComplainMapper INSTANCE = Mappers.getMapper(ComplainMapper.class);
    
    @Mapping(target = "id", ignore = true)
    ComplainEntity toEntity(Complain complain);
    
    Complain toDomain(ComplainEntity complainEntity);

    @Mapping(target = "counter", constant = "0")
    Complain toComplain(ComplainRequest request);
}
