package rw.companyz.useraccountms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import rw.companyz.useraccountms.models.dtos.CreateRoleDTO;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.io.Serial;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role extends Auditable implements GrantedAuthority {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable=false, unique=true)
    private String name;

    @Column(name="description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private EStatus status = EStatus.ACTIVE;

    @Transient
    private List<Privilege> privileges;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role(CreateRoleDTO dto) {
        this.name = dto.getName().toUpperCase();
        this.description = dto.getDescription();
    }

    @JsonIgnore
    public String getIdString() {
       return this.id.toString();
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
