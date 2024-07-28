package rw.companyz.useraccountms.audits;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
public abstract class UserDateAudit extends DateAudit {

    private UUID operatorId;

    private UUID operatorRoleId;

    private UUID operatorPrivilegeId;
}


