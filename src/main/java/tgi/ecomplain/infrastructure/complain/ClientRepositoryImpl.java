package tgi.ecomplain.infrastructure.complain;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tgi.ecomplain.domain.complain.ClientRepository;
import tgi.ecomplain.domain.complain.model.Client;

@Component
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final JpaClientRepository jpaClientRepository;

    @Override
    public Client getClient(String email) {
        Optional<ClientEntity> clientEntity = jpaClientRepository.findByEmail(email);
        return clientEntity.map(this::mapToDomain).orElse(null);
    }

    @Override
    public Client createClient(String email, String firstName, String lastName) {
        ClientEntity clientEntity = ClientEntity.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        
        ClientEntity savedEntity = jpaClientRepository.save(clientEntity);
        return mapToDomain(savedEntity);
    }
    
    private Client mapToDomain(ClientEntity entity) {
        return new Client(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail()
        );
    }
}
