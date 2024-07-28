package rw.companyz.useraccountms.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.dtos.CreatePrivilegeDTO;
import rw.companyz.useraccountms.models.dtos.DeletePrivilegeDTO;

import java.util.List;
import java.util.UUID;


public interface IPrivilegeService {
    Page<Privilege> getAllPaginated(Pageable pageable);
    List<Privilege> getAll();
    Privilege create(CreatePrivilegeDTO dto) throws DuplicateRecordException, ResourceNotFoundException;
    Page<Privilege> searchAll(String query, Pageable pageable);
    Privilege getById(UUID id) throws ResourceNotFoundException;
    void delete(DeletePrivilegeDTO dto) throws ResourceNotFoundException;
    Privilege getByName(String name) throws ResourceNotFoundException;
}
