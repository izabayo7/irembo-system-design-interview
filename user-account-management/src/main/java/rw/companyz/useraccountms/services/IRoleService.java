package rw.companyz.useraccountms.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.audits.RoleAudit;
import rw.companyz.useraccountms.models.dtos.AddOrRemovePrivilegesDTO;
import rw.companyz.useraccountms.models.dtos.CreateRoleDTO;
import rw.companyz.useraccountms.models.dtos.UpdateRoleDTO;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.util.List;
import java.util.UUID;


public interface IRoleService {
    Page<Role> getAllPaginated(Pageable pageable) throws ResourceNotFoundException;
    Page<Role> searchAll(String q, EStatus statu, Pageable pageable) throws ResourceNotFoundException;
    List<Role> getAllByStatus(EStatus status) throws ResourceNotFoundException;

    List<Privilege> getAllAttachedPrivileges(UUID roleId, String query) throws ResourceNotFoundException;
    List<Privilege> getAllNotAttachedPrivileges(UUID roleId, String query) throws ResourceNotFoundException;
    Role create(CreateRoleDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException;
    Role getById(UUID id) throws ResourceNotFoundException;
    List<RoleAudit> getAuditsById(UUID id) throws ResourceNotFoundException;

    Role getPureRole(UUID id) throws ResourceNotFoundException;

    Role addPrivileges(UUID id, AddOrRemovePrivilegesDTO dto) throws ResourceNotFoundException, DuplicateRecordException;
    Role removePrivileges(UUID id, AddOrRemovePrivilegesDTO dto) throws ResourceNotFoundException;
    Role updateById(UUID id, UpdateRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;
    Role changeStatusById(UUID id, EStatus status) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;
    void deleteById(UUID id) throws ResourceNotFoundException;

}
