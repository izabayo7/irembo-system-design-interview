package rw.companyz.useraccountms.services;

import rw.companyz.useraccountms.exceptions.BadRequestAlertException;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.UserAccountRole;
import rw.companyz.useraccountms.models.dtos.CreateUserRoleDTO;
import rw.companyz.useraccountms.models.enums.EStatus;

import java.util.List;
import java.util.UUID;

public interface IUserRoleService {
    UserAccountRole create(CreateUserRoleDTO dto,UserAccount user) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;
    void createAll(List<CreateUserRoleDTO> dtos, UserAccount user) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException;
    List<UserAccountRole> getAllByUserId(UserAccount user) throws ResourceNotFoundException;

    List<UserAccountRole> getAllActiveByUserId(UserAccount user) throws ResourceNotFoundException;

    UserAccountRole getById(UUID id) throws ResourceNotFoundException;

    UserAccountRole remove(UUID roleAccountId) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException ;

    UserAccountRole changeStatus(UUID id, EStatus status) throws ResourceNotFoundException, DuplicateRecordException, BadRequestAlertException, BadRequestException;
    void deleteById(UUID id) throws ResourceNotFoundException, DuplicateRecordException, BadRequestAlertException, BadRequestException;
}
