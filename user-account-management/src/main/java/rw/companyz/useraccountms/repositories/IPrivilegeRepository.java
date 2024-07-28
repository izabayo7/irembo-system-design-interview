package rw.companyz.useraccountms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.models.Privilege;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPrivilegeRepository extends JpaRepository<Privilege, UUID> {
    Optional<Privilege> findByName(String name);

    @Query("SELECT p FROM Privilege p WHERE (p.name ILIKE CONCAT('%', :query, '%'))")
    List<Privilege> searchAll(String query);

    @Query("SELECT p FROM Privilege p WHERE (p.name ILIKE CONCAT('%', :query, '%') OR p.description ILIKE CONCAT('%', :query, '%'))")
    Page<Privilege> searchAll(String query, Pageable pageable);
}

