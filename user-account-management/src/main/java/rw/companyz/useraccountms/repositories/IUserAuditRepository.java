package rw.companyz.useraccountms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.audits.UserAudit;

import java.util.List;
import java.util.UUID;

@Repository
public interface IUserAuditRepository extends JpaRepository<UserAudit, UUID> {
    List<UserAudit> findAllByUserAccount(UserAccount userAccount);
}
