package rw.companyz.useraccountms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.companyz.useraccountms.fileHandling.File;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFileRepository extends JpaRepository<File, UUID> {
    Optional<File> findByName(String name);
}
