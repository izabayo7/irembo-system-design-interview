package rw.companyz.useraccountms.controllers;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.annotations.VerifyUser;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.fileHandling.File;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.audits.UserAudit;
import rw.companyz.useraccountms.models.domains.ApiResponse;
import rw.companyz.useraccountms.models.dtos.*;
import rw.companyz.useraccountms.models.enums.EAttachStatus;
import rw.companyz.useraccountms.models.enums.ELoginStatus;
import rw.companyz.useraccountms.models.enums.EUserStatus;
import rw.companyz.useraccountms.services.IFileService;
import rw.companyz.useraccountms.services.IUserService;
import rw.companyz.useraccountms.utils.Constants;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final IUserService userService;

    private final IFileService fileService;

    @Operation(summary = "Get all users", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(value = "")
    public ResponseEntity<ApiResponse<Page<UserAccount>>> getAll(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) throws ResourceNotFoundException {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<UserAccount> users = this.userService.getAllPaginated(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(users, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get all paginated users by status", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(value = "/status/{status}")
    public ResponseEntity<ApiResponse<Page<UserAccount>>> getAllByStatus(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @PathVariable(value = "status") EUserStatus status) throws ResourceNotFoundException {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<UserAccount> users = this.userService.getAllByUserStatus(status, pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(users, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get all paginated users by role", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(value = "/role/{roleId}/list")
    public ResponseEntity<ApiResponse<List<UserAccount>>> getAllByRole(
            @PathVariable(value = "roleId") UUID roleId) throws ResourceNotFoundException {
        List<UserAccount> users = this.userService.getAllByRoleId(roleId);

        return ResponseEntity.ok(
                new ApiResponse<>(users, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get all attached/unatached roles on a user", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(value = "/{id}/roles/{attachStatus}")
    public ResponseEntity<ApiResponse<List<Role>>> getRoles(
            @Valid @PathVariable(value = "id") UUID userId,
            @Valid @PathVariable(value = "attachStatus") EAttachStatus attachStatus) throws ResourceNotFoundException {
        if (attachStatus == EAttachStatus.ATTACHED) {
            List<Role> roles = this.userService.getAllAttachedRoles(userId);
            return ResponseEntity.ok(
                    new ApiResponse<>(roles, localize("responses.getListSuccess"), HttpStatus.OK)
            );
        }
        else {
            List<Role> roles = this.userService.getAllNotAttachedRoles(userId);
            return ResponseEntity.ok(
                    new ApiResponse<>(roles, localize("responses.getListSuccess"), HttpStatus.OK)
            );
        }
    }

    @Operation(summary = "Change users status", description = "Privileges:  ACTIVATE_OR_DISABLE_USER")
    @PreAuthorize("hasAuthority('ACTIVATE_OR_DISABLE_USER')")
    @PutMapping(value = "/{id}/status/{status}")
    public ResponseEntity<ApiResponse<UserAccount>> changeStatus(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "status") EUserStatus status) throws ResourceNotFoundException {
        UserAccount userAccount = this.userService.changeStatusById(id, status);
        return ResponseEntity.ok(
                new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Change status of multiple users", description = "Privileges:  ACTIVATE_OR_DISABLE_USER")
    @PreAuthorize("hasAuthority('ACTIVATE_OR_DISABLE_USER')")
    @PutMapping(value = "/status/{status}")
    public ResponseEntity<ApiResponse<List<UserAccount>>> changeStatusByMultiple(
            @Valid @RequestBody MultipleIdsDTO userIds,
            @PathVariable(value = "status") EUserStatus status) throws ResourceNotFoundException {
        List<UserAccount> userAccounts = this.userService.changeStatusByMultipleIds(userIds, status);
        return ResponseEntity.ok(
                new ApiResponse<>(userAccounts, localize("responses.updateEntitySuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Upload profile picture", description = "Privileges: none \n Note: only the owner of the profile can upload profile picture")
    @PutMapping(value = "/upload/profilePicture", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserAccount>> uploadProfilePicture(@ModelAttribute NewFileDTO newFileDTO) throws Exception {
        UserAccount userAccount = this.userService.uploadProfilePicture(newFileDTO.getFile());
        return new ResponseEntity<>(new ApiResponse<>(userAccount, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Upload account verification information", description = "Privileges: none \n Note: only the owner of the profile can upload the account verification information")
    @PutMapping(value = "/upload/verification", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserAccount>> uploadProfileVerification(@ModelAttribute CreateAccountVerificationDTO dto, @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        UserAccount userAccount = this.userService.uploadAccountVerificationInfo(dto, file);
        return new ResponseEntity<>(new ApiResponse<>(userAccount, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Update account verification status", description = "Privileges: UPDATE_ACCOUNT_VERIFICATION")
    @PreAuthorize("hasAuthority('UPDATE_ACCOUNT_VERIFICATION')")
    @PutMapping(value = "/verificationStatus")
    public ResponseEntity<ApiResponse<UserAccount>> updateProfileVerification(@Valid @RequestBody UpdateAccountVerificationStatusDTO dto) throws Exception {
        UserAccount userAccount = this.userService.updateAccountVerificationStatus(dto);
        return new ResponseEntity<>(new ApiResponse<>(userAccount, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Reset account verification status", description = "Privileges: RESET_ACCOUNT_VERIFICATION")
    @PreAuthorize("hasAuthority('RESET_ACCOUNT_VERIFICATION')")
    @PutMapping(value = "/resetVerificationStatus/{id}")
    public ResponseEntity<ApiResponse<UserAccount>> resetProfileVerification(@Valid @PathVariable(value = "id") UUID userId) throws Exception {
        UserAccount userAccount = this.userService.resetAccountVerificationStatus(userId);
        return new ResponseEntity<>(new ApiResponse<>(userAccount, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a user's raw file(profile picture,....)", description = "Privileges: none")
    @GetMapping("/raw/{name}")
    @ResponseBody
    public ResponseEntity<Resource> getFileResource(@PathVariable String name) throws ResourceNotFoundException, IOException {
        File file = fileService.findByName(name);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getType()))
                .body(fileService.load(file.getPath()));
    }

    @Operation(summary = "Search users", description = """
            Privileges:  RETRIEVE_USER  
            Note: search is done on firstName, lastName, email, nationality and username
            However, you can filter by role and status.
            """)
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(value = "/search")
    public ResponseEntity<ApiResponse<Page<UserAccount>>> searchAll(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "q",required = false,defaultValue = "") String query,
            @RequestParam(value = "sortBy",required = false,defaultValue = "created_at") String sortBy,
            @RequestParam(value = "roleId", required = false) UUID roleId,
            @RequestParam(value = "status", required = false) EUserStatus status,
            @RequestParam(value = "loginStatus", required = false) ELoginStatus loginStatus,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) throws ResourceNotFoundException {

        Pageable pageable;

        if(sortBy.equals("created_at") || sortBy.equals("updated_at") || sortBy.equals("last_login")){
             pageable =  PageRequest.of(page-1, limit, Sort.by(sortBy).descending());
        }else{
                pageable = PageRequest.of(page-1, limit, Sort.by(sortBy).ascending());
        }

        Page<UserAccount> users = this.userService.searchAll(query, roleId, status, loginStatus, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(users, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get user by id", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(path="/{id}")
    public ResponseEntity<ApiResponse<UserAccount>> getById(@Valid @PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        UserAccount userAccount = this.userService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Get user audits by user id", description = "Privileges:  RETRIEVE_USER")
    @PreAuthorize("hasAuthority('RETRIEVE_USER')")
    @GetMapping(path="/{id}/audits")
    public ResponseEntity<ApiResponse<List<UserAudit>>> getAuditsById(@Valid @PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        List<UserAudit> audits = this.userService.getAuditsById(id);
        return ResponseEntity.ok(new ApiResponse<>(audits, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Get user audits of logged in user")
    @GetMapping(path="/audits/loggedInUser")
    public ResponseEntity<ApiResponse<List<UserAudit>>> getAuditsOfLoggedInUser() throws ResourceNotFoundException {
        List<UserAudit> audits = this.userService.getAuditsOfLoggedInUser();
        return ResponseEntity.ok(new ApiResponse<>(audits, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }


    @Operation(summary = "Create a user", description = "Privileges: any  of CREATE_USER, INITIATE_FLOW")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CREATE_USER','INITIATE_FLOW')")
    public ResponseEntity<ApiResponse<UserAccount>> create(@Valid @RequestBody CreateUserDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
            UserAccount userAccount = this.userService.create(dto);
            return new ResponseEntity<>(new ApiResponse<>(userAccount, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a user", description = "Privileges:  UPDATE_USER")
    @PreAuthorize("hasAuthority('UPDATE_USER') ")
    @PutMapping(path="/{id}")
    public ResponseEntity<ApiResponse<UserAccount>> updateById(@PathVariable(value = "id") UUID id, @Valid @RequestBody UpdateUserDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
            UserAccount userAccount = this.userService.updateById(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @PutMapping(path="/update/my-profile")
    public ResponseEntity<ApiResponse<UserAccount>> updateMyProfile(@Valid @RequestBody UpdateProfileDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        UserAccount userAccount = this.userService.updateMyProfile(dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Add role to a user", description = """ 
    Privileges: any  of ASSIGN_ROLE_TO_USER, INITIATE_FLOW 
    Note: In case the role was already attached to a user, the status will be changed to ACTIVE
    """)
    @PreAuthorize("hasAnyAuthority('ASSIGN_ROLE_TO_USER','INITIATE_FLOW')")
    @PutMapping(path="/{id}/addRole")
    public ResponseEntity<ApiResponse<UserAccount>> addRole(@PathVariable(value = "id") UUID id, @Valid @RequestBody CreateUserRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        UserAccount userAccount = this.userService.addRole(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Add multiple roles to a user",  description = """
    Privileges: any  of ASSIGN_ROLE_TO_USER, INITIATE_FLOW
    Note: In case the roles were already attached to a user, the status will be changed to ACTIVE
    """)
    @PreAuthorize("hasAnyAuthority('ASSIGN_ROLE_TO_USER','INITIATE_FLOW')")
    @PutMapping(path="/addRole")
    public ResponseEntity<ApiResponse<List<UserAccount>>> addRoleOnUsers( @Valid @RequestBody  CreateMultipleUserRoleDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        List<UserAccount> userAccounts = this.userService.addMultipleUsersOnRole(dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccounts, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Remove role from a user", description = """
    Privileges: any  of REVOKE_ROLE_FROM_USER, INITIATE_FLOW
    Note: The role won't be deleted from a user, instead only status will be changed to INACTIVE
    """)
    @PreAuthorize("hasAnyAuthority('REVOKE_ROLE_FROM_USER','INITIATE_FLOW')")
    @PutMapping(path="/removeRole/{userAccountRoleId}")
    public ResponseEntity<ApiResponse<UserAccount>> removeRole(@Valid @PathVariable(value = "userAccountRoleId") UUID userAccountRoleId) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        UserAccount userAccount = this.userService.removeRole(userAccountRoleId);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @PutMapping(path="/updatePassword")
    public ResponseEntity<ApiResponse<UserAccount>> changePassword( @Valid @RequestBody UpdatePasswordDTO dto) throws ResourceNotFoundException {
        UserAccount userAccount = this.userService.updatePassword(dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation (summary = "Unlock a user", description = "Privileges:  UNLOCK_USER")
    @PreAuthorize("hasAuthority('UNLOCK_USER')")
    @PutMapping(path="/{id}/unlock")
    public ResponseEntity<ApiResponse<UserAccount>> unlockUser(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        UserAccount userAccount = this.userService.unlockAccountById(id);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Unlock multiple users", description = "Privileges:  UNLOCK_USER")
    @PreAuthorize("hasAuthority('UNLOCK_USER')")
    @PutMapping(path="/unlockUsers")
    public ResponseEntity<ApiResponse<List<UserAccount>>> unlockMultipleUsers(@Valid @RequestBody MultipleIdsDTO userIds) throws ResourceNotFoundException {
        List<UserAccount> userAccounts = this.userService.unlockMultipleAccounts(userIds);
        return ResponseEntity.ok(new ApiResponse<>(userAccounts, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Reset user password", description = """
            Privileges: none
            Note: Only the owner of the profile can change password
            This will  only succeed if the user is in status PENDING or user has active password reset request
            """)
    @VerifyUser("id")
    @PutMapping(path="/{id}/setPassword")
    public ResponseEntity<ApiResponse<UserAccount>> setPassword(@PathVariable(value = "id") UUID id, @Valid @RequestBody SetPasswordDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        UserAccount userAccount = this.userService.setInitialPassword(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Reset user password by Admin", description = """
            Privileges: RESET_PASSWORD
            """)
    @PreAuthorize("hasAuthority('RESET_PASSWORD')")
    @PutMapping(path="/{id}/resetPassword")
    public ResponseEntity<ApiResponse<UserAccount>> resetPassword(@PathVariable(value = "id") UUID id, @Valid @RequestBody SetPasswordDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        UserAccount userAccount = this.userService.resetPassword(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(userAccount, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Delete user ", description = """
            Privileges:  DELETE_USER
            Note: This will only change status of a user to DELETED, however, the user will still be in the database
            """)
    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping(path="/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        this.userService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(new Object(), localize("responses.deleteEntitySuccess"), null, HttpStatus.OK));
    }

    @Operation(summary = "Delete profile picture", description = "Privileges: none \n Note: only the owner of the profile can delete profile picture")
    @DeleteMapping(path="/remove/profilePicture")
    public ResponseEntity<ApiResponse<Object>> deleteProfilePicture()throws ResourceNotFoundException {
        this.userService.removeProfilePicture();
        return ResponseEntity.ok(new ApiResponse<>(new Object(), localize("responses.updateEntitySuccess"), null, HttpStatus.OK));
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

}

