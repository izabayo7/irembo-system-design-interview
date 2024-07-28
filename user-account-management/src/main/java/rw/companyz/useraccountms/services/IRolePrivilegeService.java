package rw.companyz.useraccountms.services;

import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.*;

import java.util.List;
import java.util.UUID;

public interface IRolePrivilegeService {
    List<Privilege> create(Role role, String remarks, List<UUID> privilegesIds) throws DuplicateRecordException, ResourceNotFoundException;

    List<Privilege> getAllByRole(Role role) throws ResourceNotFoundException;

    List<Privilege> getAllAttachedPrivilegesByRole(Role role, String query) throws ResourceNotFoundException;

    void deleteById(UUID roleId, UUID privilegeId) throws ResourceNotFoundException;

    List<Privilege> getAllUnattachedPrivilegesByRole(Role role, String query) throws ResourceNotFoundException;

}
