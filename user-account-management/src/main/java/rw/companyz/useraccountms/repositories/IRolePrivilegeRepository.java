package rw.companyz.useraccountms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.RolePrivilege;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRolePrivilegeRepository extends JpaRepository<RolePrivilege, UUID> {

    @Query("SELECT rp FROM RolePrivilege rp WHERE rp.role=:role AND (rp.privilege.name ILIKE CONCAT('%', :query, '%'))")
    List<RolePrivilege> searchAllAttachedPrivilegesByRole(Role role, String query);

    List<RolePrivilege> findAllByRole(Role role);

    void deleteByRoleAndPrivilege(Role role, Privilege privilege);

    @Query("""
    SELECT p
       FROM Privilege p
       WHERE NOT EXISTS (
           SELECT 1
           FROM RolePrivilege rp
           WHERE rp.privilege.id = p.id
             AND rp.role = :role
   )
   AND (p.name ILIKE CONCAT('%', :query, '%'))
   """)
    List<Privilege> findAllUnattachedPrivilegesByRole(Role role, String query);
}
