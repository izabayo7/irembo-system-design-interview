package rw.companyz.useraccountms.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.audits.UserAudit;
import rw.companyz.useraccountms.models.dtos.*;
import rw.companyz.useraccountms.models.enums.ELoginStatus;
import rw.companyz.useraccountms.models.enums.EUserStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface IUserService {
    Page<UserAccount> searchAll(String q, UUID roleId, EUserStatus status, ELoginStatus loginStatus, Pageable pageable) throws ResourceNotFoundException;
    Page<UserAccount> getAllPaginated(Pageable pageable) throws ResourceNotFoundException;

    Page<UserAccount> getAllByUserStatus(EUserStatus status, Pageable pageable) throws ResourceNotFoundException;

    List<UserAudit> getAuditsOfLoggedInUser() throws ResourceNotFoundException;

    List<UserAudit> getAuditsById(UUID id) throws ResourceNotFoundException;

    UserAccount getPureUserById(UUID id) throws ResourceNotFoundException;

    List<Role> getAllAttachedRoles(UUID userId) throws ResourceNotFoundException;

    List<Role> getAllNotAttachedRoles(UUID userId) throws ResourceNotFoundException;

    @Transactional
    List<UserAccount> addMultipleUsersOnRole(CreateMultipleUserRoleDTO dtos) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;

    UserAccount removeRole(UUID userAccountRoleId) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;

    List<UserAccount> getAllByRoleId(UUID roleId) throws ResourceNotFoundException;

    UserAccount create(CreateUserDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException;

    UserAccount getById(UUID id) throws ResourceNotFoundException;

    UserAccount updateById(UUID id, UpdateUserDTO dto) throws ResourceNotFoundException, DuplicateRecordException;

    UserAccount uploadProfilePicture(MultipartFile document) throws Exception;
    UserAccount uploadAccountVerificationInfo(CreateAccountVerificationDTO dto, MultipartFile file) throws Exception;
    UserAccount updateAccountVerificationStatus(UpdateAccountVerificationStatusDTO dto) throws Exception;
    UserAccount resetAccountVerificationStatus(UUID id) throws Exception;

    UserAccount updateMyProfile(UpdateProfileDTO dto) throws ResourceNotFoundException, DuplicateRecordException;

    UserAccount updatePassword(UpdatePasswordDTO dto) throws ResourceNotFoundException;

    UserAccount setInitialPassword(UUID id, SetPasswordDTO dto) throws ResourceNotFoundException;

    @Transactional
    UserAccount resetPassword(UUID id, SetPasswordDTO passwordDTO) throws ResourceNotFoundException;

    UserAccount changeStatusById(UUID id, EUserStatus status) throws ResourceNotFoundException;

    List<UserAccount> changeStatusByMultipleIds(MultipleIdsDTO userIds, EUserStatus status) throws ResourceNotFoundException;

    UserAccount unlockAccountById(UUID id) throws ResourceNotFoundException;

    List<UserAccount> unlockMultipleAccounts(MultipleIdsDTO userIds) throws ResourceNotFoundException;

    void deleteById(UUID id) throws ResourceNotFoundException;

    UserAccount removeProfilePicture() throws ResourceNotFoundException;

    UserAccount addRole(UUID id, CreateUserRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException;

    UserAccount getLoggedInUser() throws ResourceNotFoundException;
}