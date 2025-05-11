package tgi.ecomplain.domain.complain;

import tgi.ecomplain.domain.complain.model.Complain;

public interface ComplainRepository {
    Complain saveComplain(Complain complain);
}
