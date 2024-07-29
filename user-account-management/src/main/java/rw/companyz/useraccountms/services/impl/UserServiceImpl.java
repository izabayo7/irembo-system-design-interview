package rw.companyz.useraccountms.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.*;
import rw.companyz.useraccountms.fileHandling.File;
import rw.companyz.useraccountms.models.*;
import rw.companyz.useraccountms.models.audits.UserAudit;
import rw.companyz.useraccountms.models.dtos.*;
import rw.companyz.useraccountms.models.enums.*;
import rw.companyz.useraccountms.repositories.*;
import rw.companyz.useraccountms.security.dtos.CustomUserDTO;
import rw.companyz.useraccountms.services.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;

    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private final IRolePrivilegeService rolePrivilegeService;
    private final IFileService fileService;
    private final IJwtService jwtService;
    private final IUserRoleService userRoleReService;

    private final IUserAuditRepository userAuditRepository;

    private final IAuthenticationService authenticationService;

    private final IEmailService emailService;

    ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceImpl(IUserRepository userRepository, IUserRoleRepository userRoleRepository, IUserAuditRepository userAuditRepository, IRoleService roleService, PasswordEncoder passwordEncoder, IRolePrivilegeService rolePrivilegeService, IFileService fileService, IJwtService jwtService, IUserRoleService userRoleReService, @Lazy IAuthenticationService authenticationService, IEmailService emailService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userAuditRepository = userAuditRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.rolePrivilegeService = rolePrivilegeService;
        this.fileService = fileService;
        this.jwtService = jwtService;
        this.userRoleReService = userRoleReService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }

    @Override
    public Page<UserAccount> getAllPaginated(Pageable pageable) throws ResourceNotFoundException {
        Page<UserAccount> users = this.userRepository.findAllByStatusNot(EUserStatus.DELETED, pageable);

        return users;
    }

    @Override
    public Page<UserAccount> searchAll(String q, UUID roleId, EUserStatus status, ELoginStatus loginStatus, Pageable pageable) throws ResourceNotFoundException {
        return this.userRepository.searchAll(pageable, q, status == null ? null: status.name(), loginStatus == null ? null : loginStatus.name(),roleId);
    }

    @Override
    public Page<UserAccount> getAllByUserStatus(EUserStatus status, Pageable pageable) throws ResourceNotFoundException {
        Page<UserAccount> users = this.userRepository.findAllByStatus(status, pageable);

        return users;
    }

    @Override
    public List<UserAccount> getAllByRoleId(UUID roleId) throws ResourceNotFoundException {
        Role role = this.roleService.getById(roleId);

        return this.userRepository.findAllByRole(role);
    }

    @Override
    public List<Role> getAllAttachedRoles(UUID userId) throws ResourceNotFoundException {
        UserAccount u = this.getById(userId);
        return this.getRolesFromUser(u);
    }

    @Override
    public List<Role> getAllNotAttachedRoles(UUID userId) throws ResourceNotFoundException {
        UserAccount u = this.getById(userId);

        List<Role> roles = this.roleService.getAllByStatus(EStatus.ACTIVE);
        List<Role> attachedRoles = this.getRolesFromUser(u);

        roles.removeIf(item -> attachedRoles.stream()
                .anyMatch(obj -> obj.getId().toString().equals(item.getId().toString())));

        return roles;
    }



    @Override
    @Transactional
    public UserAccount create(CreateUserDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        dto.setEmailAddress(dto.getEmailAddress().trim());
        dto.setPassword(dto.getPassword().trim());

        Optional<UserAccount> duplicateEmailAddress = this.userRepository.findByEmailAddress(dto.getEmailAddress());
        if (duplicateEmailAddress.isPresent())
            throw new DuplicateRecordException("User", "emailAddress", dto.getEmailAddress());

        // TODO: make this optional (normal users don't need roles)
        for (CreateUserRoleDTO roleId: dto.getRoleIds()) {
            this.roleService.getPureRole(roleId.getRoleId());
        }

        UserAccount userAccount = new UserAccount(dto);

        userAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
        userAccount.setCredentialsExpired(false);
        userAccount.setCredentialsExpiryDate(LocalDateTime.now().plusMonths(12).toString());
        userAccount.setAccountEnabled(true);
        userAccount.setAccountExpired(false);
        userAccount.setAccountLocked(false);
        userAccount.setDeletedFlag(false);
        userAccount = this.userRepository.save(userAccount);

        if (!dto.getRoleIds().isEmpty())
            this.userRoleReService.createAll(dto.getRoleIds(), userAccount);

        try {
            CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
            UserAudit audit = new UserAudit(userAccount, EAuditType.CREATE, userDTO.getId(), userDTO.getFullNames(), "CREATE_USER", "New user created", null);
            this.userAuditRepository.save(audit);
        } catch (Exception e){
            // Can't extract the logged-in user on sign up
        }

        this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Account Created", "Your account has been created.", "Please login to continue.", "/login", "Continue to login");

        return userAccount;
    }

    @Override
    public UserAccount getById(UUID id) throws ResourceNotFoundException {
        UserAccount userAccount = this.getPureUserById(id);
        List<UserAccountRole> userRoles = userRoleReService.getAllByUserId(userAccount);

        for(UserAccountRole userAccountRole: userRoles){
            userAccountRole.getRole().setPrivileges(this.rolePrivilegeService.getAllByRole(userAccountRole.getRole()));
        }
        userAccount.setRoles(userRoles);

        return userAccount;
    }

    @Override
    public UserAccount getPureUserById(UUID id) throws ResourceNotFoundException {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );
    }

    @Override
    public List<UserAudit> getAuditsById(UUID id) throws ResourceNotFoundException {
        UserAccount userAccount = this.getPureUserById(id);
        return this.userAuditRepository.findAllByUserAccount(userAccount);
    }

    @Override
    public List<UserAudit> getAuditsOfLoggedInUser() throws ResourceNotFoundException {
        UserAccount userAccount = getPureUserById(jwtService.extractLoggedInUser().getId());
        return this.userAuditRepository.findAllByUserAccount(userAccount);
    }

    @Override
    public UserAccount uploadProfilePicture(MultipartFile document) throws Exception {
        UserAccount userAccount = getLoggedInUser();
        File file = this.fileService.create(document);
        userAccount.setProfilePicture(file);

        return this.userRepository.save(userAccount);
    }

    @Override
    @Transactional
    public UserAccount uploadAccountVerificationInfo(CreateAccountVerificationDTO dto) throws Exception {
        UserAccount userAccount = getLoggedInUser();

        if (userAccount.getVerificationStatus() != EVerificationStatus.UNVERIFIED) {
            throw new BadRequestAlertException("exceptions.badRequest.verificationAlreadySubmitted");
        }

        File file = this.fileService.create(dto.getFile());

        userAccount.setNidOrPassport(dto.getNidOrPassport());
        userAccount.setProfilePicture(file);
        userAccount.setVerificationStatus(EVerificationStatus.PENDING_VERIFICATION);

        userAccount = this.userRepository.save(userAccount);

        UserAudit audit = new UserAudit(userAccount, EAuditType.DELETE, userAccount.getId(), userAccount.getFullName(), "", "User Account Verification Info uploaded", file);
        this.userAuditRepository.save(audit);

        return userAccount;
    }

    @Override
    @Transactional
    public UserAccount updateAccountVerificationStatus(UpdateAccountVerificationStatusDTO dto) throws Exception {
        UserAccount userAccount = this.getById(dto.getUserId());

        if (userAccount.getVerificationStatus() != EVerificationStatus.PENDING_VERIFICATION) {
            throw new BadRequestAlertException("exceptions.badRequest.verificationNotPending");
        }

        userAccount.setVerificationStatus(dto.getVerificationStatus());

        userAccount = this.userRepository.save(userAccount);

        UserAudit audit = new UserAudit(userAccount, dto.getVerificationStatus() == EVerificationStatus.VERIFIED ? EAuditType.VERIFY : EAuditType.UPDATE, userAccount.getId(), userAccount.getFullName(), "", "User Account Verification Status updated", null);
        this.userAuditRepository.save(audit);

        if(dto.getVerificationStatus() == EVerificationStatus.VERIFIED){
            this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Account Verified", "Your account has been verified.", "Please login to continue.", null, "Continue to login");
        } else {
            this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Account Verification Failed", "Your account verification has failed.", "Reason: "+ dto.getRejectionReason(), null, "Login to retry");
        }

        return userAccount;
    }

    @Override
    @Transactional
    public UserAccount resetAccountVerificationStatus(UUID id) throws Exception {
        UserAccount userAccount = this.getById(id);

        if (userAccount.getVerificationStatus() != EVerificationStatus.VERIFIED) {
            throw new BadRequestAlertException("exceptions.badRequest.accountNotVerified");
        }

        userAccount.setVerificationStatus(EVerificationStatus.PENDING_VERIFICATION);

        userAccount = this.userRepository.save(userAccount);

        UserAudit audit = new UserAudit(userAccount, EAuditType.RESET, userAccount.getId(), userAccount.getFullName(), "", "User Account Verification Status reset", null);
        this.userAuditRepository.save(audit);

        return userAccount;
    }

    @Override
    public UserAccount removeProfilePicture() throws ResourceNotFoundException {
        UserAccount userAccount = this.getLoggedInUser();
        this.fileService.getById(userAccount.getProfilePicture().getId());
        userAccount.setProfilePicture(null);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.DELETE, userDTO.getId(), userDTO.getFullNames(), "", "User Profile Picture removed", null);
        this.userAuditRepository.save(audit);

        return this.userRepository.save(userAccount);
    }

    @Override
    public UserAccount updateById(UUID id, UpdateUserDTO dto) throws ResourceNotFoundException, DuplicateRecordException {
        UserAccount userAccount = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );

        // never update email address
        userAccount.setFirstName(dto.getFirstName());
        userAccount.setLastName(dto.getLastName());
        userAccount.setGender(dto.getGender());
        userAccount.setNationality(dto.getNationality());
        userAccount.setMaritalStatus(dto.getMaritalStatus());
        userAccount.setDateOfBirth(dto.getDateOfBirth());

        this.userRepository.save(userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "UPDATE_USER", "User details updated", null);
        this.userAuditRepository.save(audit);

       return userAccount;
    }

    @Override
    public UserAccount updateMyProfile(UpdateProfileDTO dto) throws ResourceNotFoundException, DuplicateRecordException {
        UserAccount userAccount = getLoggedInUser();

        // never update email address
        userAccount.setFirstName(dto.getFirstName());
        userAccount.setLastName(dto.getLastName());
        userAccount.setGender(dto.getGender());
        userAccount.setNationality(dto.getNationality());
        userAccount.setMaritalStatus(dto.getMaritalStatus());
        userAccount.setDateOfBirth(dto.getDateOfBirth());
        userAccount.setMfaEnabled(dto.isMfaEnabled());

        this.userRepository.save(userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "UPDATE_USER", "User details updated", null);
        this.userAuditRepository.save(audit);

        return userAccount;
    }

    @Override
    public UserAccount updatePassword(UpdatePasswordDTO dto) throws ResourceNotFoundException {
        UserAccount userAccount = getLoggedInUser();

        if(!passwordEncoder.matches(dto.getOldPassword(), userAccount.getPassword())) {
            throw new BadRequestAlertException("exceptions.badRequest.passwordMissMatch");
        }
        userAccount.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.userRepository.save(userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "UPDATE_USER", "User Password updated", null);
        this.userAuditRepository.save(audit);

        this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Password Updated", "Your password has been updated.", "Please login with your new password.", null, "Continue to login");

        return userAccount;
    }

    @Override
    public UserAccount addRole(UUID id, CreateUserRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException  {
        UserAccount userAccount = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );

        this.userRoleReService.create(dto, userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "ASSIGN_ROLE_TO_USER", "Add user role", null);
        this.userAuditRepository.save(audit);

        return userAccount;
    }

    @Override
    @Transactional
    public List<UserAccount> addMultipleUsersOnRole(CreateMultipleUserRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        List<UserAccount> userAccounts = new ArrayList<>();
        UserAccount userAccount = null;

        for(UUID userId: dto.getUserIds()){
            userAccount = this.userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", userId.toString())
            );

            this.userRoleReService.create(dto.toCreateUserRoleDTO(), userAccount);
            userAccounts.add(userAccount);
        }

        return userAccounts;
    }

    @Override
    public UserAccount removeRole(UUID userAccountRoleId) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        UserAccountRole userAccountRole = this.userRoleReService.remove(userAccountRoleId);

        UserAccount userAccount = userAccountRole.getUser();

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "REVOKE_ROLE_FROM_USER", "Remove user role", null);
        this.userAuditRepository.save(audit);

        return userAccount;
    }


    @Override
    public UserAccount setInitialPassword(UUID id, SetPasswordDTO dto) throws ResourceNotFoundException {
        UserAccount userAccount = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );

        if (userAccount.getStatus().equals(EUserStatus.PENDING)) { // password reset initiated by admin or first time user
            userAccount.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userAccount.setStatus(EUserStatus.ACTIVE);
        }else{ // forgot password initiated by a user
            if (LocalDateTime.now().isAfter(userAccount.getAuthTokenExpiryDate())) {
                throw new InvalidCredentialsException("Login token has expired");
            }
            userAccount.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        this.userRepository.save(userAccount);

        return userAccount;
    }

    @Override
    @Transactional
    public UserAccount resetPassword(UUID id,SetPasswordDTO passwordDTO) throws ResourceNotFoundException {
        UserAccount userAccount = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );

        if (userAccount.getStatus().equals(EUserStatus.ACTIVE) || userAccount.getStatus().equals(EUserStatus.PENDING) ) { // password reset initiated by admin or first time user
            userAccount.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userAccount.setStatus(EUserStatus.PENDING);
            userAccount = this.userRepository.save(userAccount);

            authenticationService.invalidateUserLogin(userAccount);

            UserAudit audit = new UserAudit(userAccount, EAuditType.RESET, userAccount.getId(), userAccount.getFullName(), "RESET_PASSWORD", "Reset user's password", null);
            this.userAuditRepository.save(audit);

            this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Password Reset", "Your password has been reset.", "Please login with your new password.", null, "Continue to login");


            return userAccount;

        }else{
            throw new BadRequestAlertException("exceptions.invalidUserResetPassword");
        }
    }

    
    @Override
    public UserAccount changeStatusById(UUID id, EUserStatus status) throws ResourceNotFoundException {
        UserAccount userAccount = this.getPureUserById(id);
        if (status == EUserStatus.DELETED || status == EUserStatus.PENDING || status == EUserStatus.LOGIN_LOCKED) throw new BadRequestAlertException("exceptions.badRequest.invalidStatus");
        userAccount.setStatus(status);

        if(status == EUserStatus.ACTIVE){
            userAccount.setAccountEnabled(true);
            userAccount.setAccountExpired(false);
            userAccount.setAccountLocked(false);
            userAccount.setCredentialsExpired(false);
            userAccount.setCredentialsExpiryDate(LocalDateTime.now().plusMonths(12).toString());
        }


        if(status == EUserStatus.ADMIN_LOCKED){
            userAccount.setAccountLocked(true);
            authenticationService.invalidateUserLogin(userAccount);

            this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Account Locked", "Your account has been locked.", "Please contact the admin for more information.", null, "Contact Admin");
        }

        if(status == EUserStatus.INACTIVE){
            userAccount.setAccountEnabled(false);
            authenticationService.invalidateUserLogin(userAccount);

            this.emailService.sendHtmlMessage(userAccount.getEmailAddress(), "Account Deactivated", "Your account has been deactivated.", "Please contact the admin for more information.", null, "Contact Admin");
        }

         userAccount=this.userRepository.save(userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, (status == EUserStatus.ACTIVE) ? EAuditType.ACTIVATE : EAuditType.DISABLE, userDTO.getId(), userDTO.getFullNames(), "ACTIVATE_OR_DISABLE_USER", "User status changed", null);
        this.userAuditRepository.save(audit);


        return userAccount;
    }

    @Override
    public List<UserAccount> changeStatusByMultipleIds(MultipleIdsDTO userIds, EUserStatus status) throws ResourceNotFoundException {
        List<UserAccount> userAccounts = new ArrayList<>();

        for(UUID userId: userIds.getIds()){
            userAccounts.add(changeStatusById(userId, status));
        }

        return userAccounts;
    }

    @Override
    public UserAccount unlockAccountById(UUID id) throws ResourceNotFoundException {
        UserAccount userToUnlock = this.getPureUserById(id);

        if(userToUnlock.getStatus().equals(EUserStatus.LOGIN_LOCKED) || userToUnlock.getStatus().equals(EUserStatus.ADMIN_LOCKED)) {
            userToUnlock.setStatus(EUserStatus.PENDING);
            userToUnlock.setAccountLocked(false);


            userToUnlock = this.userRepository.save(userToUnlock);

            CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
            UserAudit audit = new UserAudit(userToUnlock, EAuditType.ACTIVATE , userDTO.getId(), userDTO.getFullNames(), "LOCK_USER", "User is unlocked", null);
            this.userAuditRepository.save(audit);

            this.emailService.sendHtmlMessage(userToUnlock.getEmailAddress(), "Account Unlocked", "Your account has been unlocked.", "Please login to continue.", null, "Continue to login");
        }else{
            throw new BadRequestAlertException("exceptions.badRequest.invalidStatus");
        }
        return userToUnlock;
    }

    @Override
    public  List<UserAccount> unlockMultipleAccounts(MultipleIdsDTO userIds) throws ResourceNotFoundException {
        List<UserAccount> unlockedUsers = new ArrayList<>();

        for(UUID userId: userIds.getIds()){
            unlockedUsers.add(unlockAccountById(userId));
        }

        return unlockedUsers;
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException {
        UserAccount userAccount = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString())
        );

        String uniqueIdentifier = UUID.randomUUID().toString();

        userAccount.setStatus(EUserStatus.DELETED);
        userAccount.setDeletedFlag(true);

        userAccount = this.userRepository.save(userAccount);

        authenticationService.invalidateUserLogin(userAccount);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        UserAudit audit = new UserAudit(userAccount, EAuditType.DELETE, userDTO.getId(), userDTO.getFullNames(), "DELETE_USER", "User Delete changed", null);
        this.userAuditRepository.save(audit);
    }
    @Override
    public UserAccount getLoggedInUser() throws ResourceNotFoundException {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Optional<UserAccount> findByEmail = userRepository.findByEmailAddress(username);
        if (findByEmail.isPresent()) {
            return this.getById(findByEmail.get().getId());
        }
        else {
            return null;
        }
    }

    private List<Role> getRolesFromUser(UserAccount u) {
        List<Role> roles = new ArrayList<>();
        for (UserAccountRole r : u.getRoles()) {
            if (r.getStatus() == EStatus.ACTIVE)
                roles.add(r.getRole());
        }
        return roles;
    }

}
