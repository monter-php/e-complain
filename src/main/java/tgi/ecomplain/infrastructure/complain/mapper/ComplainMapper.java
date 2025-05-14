package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.infrastructure.complain.ComplainEntity;

@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface ComplainMapper {

    @Mapping(source = "id", target = "complainId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "status", expression = "java(complainEntity.getStatus() != null ? complainEntity.getStatus().getValue() : null)")
    Complain toDomain(ComplainEntity complainEntity);
    
}
