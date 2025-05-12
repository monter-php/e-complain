package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.infrastructure.complain.ClientEntity;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    @Mapping(target = "id", ignore = true) // Ignore ID when mapping Client to ClientEntity
    ClientEntity toEntity(Client client);

    
    Client toDomain(ClientEntity clientEntity);
}
