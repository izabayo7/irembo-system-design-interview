package rw.companyz.useraccountms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.audits.RoleAudit;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRoleAuditRepository extends JpaRepository<RoleAudit, UUID> {
    List<RoleAudit> findAllByRole(Role role);
}
