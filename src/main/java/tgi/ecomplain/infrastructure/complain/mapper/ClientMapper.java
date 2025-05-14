package tgi.ecomplain.infrastructure.complain.mapper;

import org.mapstruct.Mapper;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.infrastructure.complain.ClientEntity;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toDomain(ClientEntity clientEntity);
}
