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
        Optional<ClientEntity> clientEntity = jpaClientRepository.findByEmail(complain.getClient().email());
        if (clientEntity.isEmpty()) {
            clientEntity = Optional.of(
                ClientEntity.builder()
                    .firstName(complain.getClient().firstName())
                    .lastName(complain.getClient().lastName())
                    .email(complain.getClient().email())
                    .build()
            );
        }

        // Create and save the complain entity
        ComplainEntity complainEntity;
        if(jpaComplainRepository.findOneByProductIdAndClient(complain.getProductId(), clientEntity.get()).isPresent()) {
            complainEntity = jpaComplainRepository.findOneByProductIdAndClient(complain.getProductId(), clientEntity.get()).get();
            complainEntity.setCounter(complainEntity.getCounter() + 1);
        } else {
            complainEntity = ComplainEntity.builder()
                    .productId(complain.getProductId())
                    .message(complain.getMessage())
                    .creationDate(complain.getCreationDate())
                    .client(clientEntity.get())
                    .country(complain.getCountry())
                    .counter(complain.getCounter())
                    .status(complain.getStatus() != null ? ComplainStatus.valueOf(complain.getStatus()) : ComplainStatus.SUBMITTED)
                    .build();
        }


        // Save the entity
        ComplainEntity savedEntity = jpaComplainRepository.save(complainEntity);

        // Map back to domain model
        return Complain.builder()
                .productId(savedEntity.getProductId())
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
        Optional<ClientEntity> clientEntity = jpaClientRepository.findByEmail(email);
        if (clientEntity.isEmpty()) {
            return Collections.emptyList();
        }
        List<ComplainEntity> complainEntities = jpaComplainRepository.findByClient(clientEntity.get());
        return complainEntities.stream()
                .map(infraComplainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Complain> findById(Long id) {
        return jpaComplainRepository.findById(id)
                .map(infraComplainMapper::toDomain);
    }

    @Override
    public Optional<Complain> findComplainByProductIdAndEmail(String productId, String email) {
        Optional<ClientEntity> clientEntity = jpaClientRepository.findByEmail(email);
        return clientEntity.flatMap(entity -> jpaComplainRepository.findOneByProductIdAndClient(productId, entity)
                .map(infraComplainMapper::toDomain));
    }
}
