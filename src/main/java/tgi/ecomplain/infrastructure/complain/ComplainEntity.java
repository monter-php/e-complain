package tgi.ecomplain.infrastructure.complain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgi.ecomplain.domain.complain.ComplainStatus;

import java.util.Date;

@Entity
@Table(name = "complains",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complain_client_product", columnNames = {"client_id", "product_id"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplainEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplainStatus status;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;
    
    @Column(nullable = false)
    private String country;
    
    @Column(nullable = false)
    private int counter;
}
