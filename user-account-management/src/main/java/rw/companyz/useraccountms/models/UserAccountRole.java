package rw.companyz.useraccountms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserAccountRole  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private EStatus status = EStatus.ACTIVE;


    public UserAccountRole(Role role, UserAccount user) {
        this.role = role;
        this.user = user;
        this.status = EStatus.ACTIVE;
    }
}
