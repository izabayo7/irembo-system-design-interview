package rw.companyz.useraccountms.models.audits.embeddables;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.util.stream.Collectors;

@Embeddable
@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class RoleEmbeddable {
    @Column(name = "_name")
    private String name;

    @Column(name = "_description")
    private String description;
    
    @Lob
    @Column(name = "_privileges", columnDefinition = "TEXT")
    private String privileges;

    @Column(name = "_status")
    @Enumerated(EnumType.STRING)
    private EStatus status;


    public RoleEmbeddable(@NotNull Role role) {
        this.name = role.getName();
        this.description = role.getDescription();
        this.status = role.getStatus();
        this.privileges =  role.getPrivileges() != null ? role.getPrivileges().stream()
                .map(Privilege::getName)
                .collect(Collectors.joining(", ")) : null;
    }
}

