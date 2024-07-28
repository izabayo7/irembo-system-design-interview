package rw.companyz.useraccountms.models.audits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import rw.companyz.useraccountms.audits.AuditDetail;
import rw.companyz.useraccountms.fileHandling.File;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.audits.embeddables.RoleEmbeddable;
import rw.companyz.useraccountms.models.enums.EAuditType;

import java.io.Serial;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class RoleAudit extends AuditDetail<RoleEmbeddable> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator = "RoleAuditUUID")
    @GenericGenerator(name="RoleAuditUUID", strategy="org.hibernate.id.UUIDGenerator")
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Role role;


    public RoleAudit(
            Role role, EAuditType auditType,
            UUID operatorId, String operatorNames, String operatorPrivilege,
            String observation, File supportingDocument
    ) {
        this.role = role;
        this.setSnapshot(new RoleEmbeddable(role));
        this.setAuditType(auditType);
        this.setOperatorId(operatorId);
        this.setOperationNames(operatorNames);
        this.setOperatorPrivilege(operatorPrivilege);
        this.setObservation(observation);
        this.setSupportingDocument(supportingDocument);
    }

}

