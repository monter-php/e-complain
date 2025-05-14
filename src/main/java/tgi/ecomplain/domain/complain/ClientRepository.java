package tgi.ecomplain.domain.complain;

import tgi.ecomplain.domain.complain.model.Client;

public interface ClientRepository {
    Client getClient(String email);

    Client createClient(String email, String firstName, String lastName);
}
