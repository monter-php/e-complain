package tgi.ecomplain.infrastructure.complain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaComplainRepository extends JpaRepository<ComplainEntity, Long> {
}
