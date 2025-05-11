package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.infrastructure.complain.ClientEntity;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);
    

    ClientEntity toEntity(Client client);

    
    Client toDomain(ClientEntity clientEntity);
}
