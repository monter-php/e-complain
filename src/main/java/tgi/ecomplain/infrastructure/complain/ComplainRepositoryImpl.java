package tgi.ecomplain.infrastructure.complain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tgi.ecomplain.domain.complain.ComplainRepository;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.infrastructure.complain.mapper.ComplainMapper;
import tgi.ecomplain.domain.complain.ComplainStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ComplainRepositoryImpl implements ComplainRepository {

    private final JpaComplainRepository jpaComplainRepository;
    private final JpaClientRepository jpaClientRepository;
    private final ComplainMapper infraComplainMapper;


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
                .status(complain.status() != null ? ComplainStatus.valueOf(complain.status()) : ComplainStatus.SUBMITTED)
                .build();

        // Save the entity
        ComplainEntity savedEntity = jpaComplainRepository.save(complainEntity);

        // Map back to domain model
        return Complain.builder()
                .complainId(savedEntity.getId())
                .message(savedEntity.getMessage())
                .creationDate(savedEntity.getCreationDate())
                .client(Client.builder()
                        .firstName(savedEntity.getClient().getFirstName())
                        .lastName(savedEntity.getClient().getLastName())
                        .email(savedEntity.getClient().getEmail())
                        .build())
                .status(savedEntity.getStatus().getValue())
                .country(savedEntity.getCountry())
                .counter(savedEntity.getCounter())
                .build();
    }

    @Override
    public List<Complain> findComplainsByEmail(String email) {
        ClientEntity clientEntity = jpaClientRepository.findByEmail(email);
        if (clientEntity == null) {
            return Collections.emptyList();
        }
        List<ComplainEntity> complainEntities = jpaComplainRepository.findByClient(clientEntity);
        return complainEntities.stream()
                .map(infraComplainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Complain> findById(Long id) {
        return jpaComplainRepository.findById(id)
                .map(infraComplainMapper::toDomain);
    }
}
