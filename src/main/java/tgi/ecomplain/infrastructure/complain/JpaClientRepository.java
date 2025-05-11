package tgi.ecomplain.infrastructure.complain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaClientRepository extends JpaRepository<ClientEntity, Long> {
    ClientEntity findByEmail(String email);
}
