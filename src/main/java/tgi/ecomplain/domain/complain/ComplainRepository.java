package tgi.ecomplain.domain.complain;

import tgi.ecomplain.domain.complain.model.Complain;
import java.util.List;
import java.util.Optional;

public interface ComplainRepository {
    Complain saveComplain(Complain complain);
    List<Complain> findComplainsByEmail(String email);
    Optional<Complain> findById(Long id);

    Optional<Complain> findComplainByProductIdAndEmail(String productId, String email);
}
