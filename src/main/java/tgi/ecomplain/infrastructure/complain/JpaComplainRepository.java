package tgi.ecomplain.infrastructure.complain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaComplainRepository extends JpaRepository<ComplainEntity, Long> {
    List<ComplainEntity> findByClient(ClientEntity client);
    Optional<ComplainEntity> findOneByProductIdAndClient(String productId, ClientEntity client);
}
