package tgi.ecomplain.infrastructure.complain;

import org.springframework.stereotype.Component;
import tgi.ecomplain.domain.complain.ClientRepository;
import tgi.ecomplain.domain.complain.model.Client;

@Component
public class ClientRepositoryImpl implements ClientRepository {

    private final JpaClientRepository jpaClientRepository;

    public ClientRepositoryImpl(JpaClientRepository jpaClientRepository) {
        this.jpaClientRepository = jpaClientRepository;
    }

    @Override
    public Client getClient(String email) {
        ClientEntity clientEntity = jpaClientRepository.findByEmail(email);
        if (clientEntity == null) {
            return null;
        }
        return mapToDomain(clientEntity);
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
