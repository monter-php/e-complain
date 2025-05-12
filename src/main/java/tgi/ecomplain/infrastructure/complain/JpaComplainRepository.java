package tgi.ecomplain.infrastructure.complain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaComplainRepository extends JpaRepository<ComplainEntity, Long> {
    List<ComplainEntity> findByClient(ClientEntity client);
}
