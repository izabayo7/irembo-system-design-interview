package rw.companyz.useraccountms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    List<Role> findAllByStatus(EStatus status);

    Page<Role> findAllByStatusNot(EStatus status, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE (r.name ILIKE CONCAT('%', :query, '%') OR r.description ILIKE CONCAT('%', :query, '%'))" +
            " AND r.status <> 'DELETED'")
    List<Role> searchAll(String query);

    @Query("SELECT r FROM Role r WHERE (r.name ILIKE CONCAT('%', :query, '%') OR r.description ILIKE CONCAT('%', :query, '%')) AND (:status IS NULL OR r.status = :status)")
    List<Role> searchAll(String query, EStatus status);

    @Query("SELECT r FROM Role r WHERE (r.name ILIKE CONCAT('%', :query, '%') OR r.description ILIKE CONCAT('%', :query, '%')) AND (:status IS NULL OR r.status = :status)")
    Page<Role> searchAll(String query, EStatus status, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE (:status IS NULL OR r.status = :status)")
    List<Role> searchAllByStatus(EStatus status);

    Optional<Role> findByName(String name);
}
