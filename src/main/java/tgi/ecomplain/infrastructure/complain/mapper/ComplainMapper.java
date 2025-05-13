package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.infrastructure.complain.ComplainEntity;
import tgi.ecomplain.domain.complain.ComplainStatus;

@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface ComplainMapper {
    
    @Mapping(source = "complainId", target = "id")
    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "status", expression = "java(complain.status() != null ? tgi.ecomplain.domain.complain.ComplainStatus.valueOf(complain.status()) : null)")
    ComplainEntity toEntity(Complain complain);
    
    @Mapping(source = "id", target = "complainId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "status", expression = "java(complainEntity.getStatus() != null ? complainEntity.getStatus().getValue() : null)")
    Complain toDomain(ComplainEntity complainEntity);

    @Mapping(target = "complainId", ignore = true)
    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "counter", constant = "0")
    Complain toComplain(ComplainRequest request);
}
