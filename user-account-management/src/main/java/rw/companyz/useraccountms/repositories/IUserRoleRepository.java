package rw.companyz.useraccountms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.UserAccountRole;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserAccountRole, UUID> {

    Optional<UserAccountRole> findByUserAndRole(UserAccount account, Role role);
    List<UserAccountRole> findAllByUserAndStatus(UserAccount user, EStatus active);

    List<UserAccountRole> findByRole(Role role);

    @Query("SELECT uar FROM UserAccountRole uar WHERE uar.user = :user AND uar.status = 'ACTIVE' AND uar.role.status = 'ACTIVE'")
    List<UserAccountRole> findAllActiveByUser(UserAccount user);
}
