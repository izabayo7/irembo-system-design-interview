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
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.audits.embeddables.UserEmbeddable;
import rw.companyz.useraccountms.models.enums.EAuditType;

import java.io.Serial;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class UserAudit extends AuditDetail<UserEmbeddable> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator = "RoleAuditUUID")
    @GenericGenerator(name="RoleAuditUUID", strategy="org.hibernate.id.UUIDGenerator")
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserAccount userAccount;


    public UserAudit(
            UserAccount userAccount, EAuditType auditType,
            UUID operatorId, String operatorNames, String operatorPrivilege,
            String observation, File supportingDocument
    ) {
        this.userAccount = userAccount;
        this.setSnapshot(new UserEmbeddable(userAccount));
        this.setAuditType(auditType);
        this.setOperatorId(operatorId);
        this.setOperationNames(operatorNames);
        this.setOperatorPrivilege(operatorPrivilege);
        this.setObservation(observation);
        this.setSupportingDocument(supportingDocument);
    }
}

