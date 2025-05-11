package tgi.ecomplain.infrastructure.complain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "complains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplainEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;
    
    @Column(nullable = false)
    private String country;
    
    @Column(nullable = false)
    private int counter;
}
