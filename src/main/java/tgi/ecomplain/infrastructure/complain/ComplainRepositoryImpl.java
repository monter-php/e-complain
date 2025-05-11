package tgi.ecomplain.infrastructure.complain;

import org.springframework.stereotype.Component;
import tgi.ecomplain.domain.complain.ComplainRepository;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.complain.model.Client;


@Component
public class ComplainRepositoryImpl implements ComplainRepository {

    private final JpaComplainRepository jpaComplainRepository;
    private final JpaClientRepository jpaClientRepository;

    public ComplainRepositoryImpl(JpaComplainRepository jpaComplainRepository, JpaClientRepository jpaClientRepository) {
        this.jpaComplainRepository = jpaComplainRepository;
        this.jpaClientRepository = jpaClientRepository;
    }

    @Override
    public Complain saveComplain(Complain complain) {
        // Find or create the client entity
        ClientEntity clientEntity = jpaClientRepository.findByEmail(complain.client().email());
        if (clientEntity == null) {
            clientEntity = ClientEntity.builder()
                    .firstName(complain.client().firstName())
                    .lastName(complain.client().lastName())
                    .email(complain.client().email())
                    .build();
        }

        // Create and save the complain entity
        ComplainEntity complainEntity = ComplainEntity.builder()
                .message(complain.message())
                .creationDate(complain.creationDate())
                .client(clientEntity)
                .country(complain.country())
                .counter(complain.counter())
                .build();

        // Save the entity
        ComplainEntity savedEntity = jpaComplainRepository.save(complainEntity);

        // Map back to domain model
        return Complain.builder()
                .complainId(savedEntity.getId().intValue())
                .message(savedEntity.getMessage())
                .creationDate(savedEntity.getCreationDate())
                .client(Client.builder()
                        .firstName(savedEntity.getClient().getFirstName())
                        .lastName(savedEntity.getClient().getLastName())
                        .email(savedEntity.getClient().getEmail())
                        .build())
                .country(savedEntity.getCountry())
                .counter(savedEntity.getCounter())
                .build();
    }
}
