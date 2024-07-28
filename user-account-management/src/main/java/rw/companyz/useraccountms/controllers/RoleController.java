package rw.companyz.useraccountms.controllers;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.audits.RoleAudit;
import rw.companyz.useraccountms.models.domains.ApiResponse;
import rw.companyz.useraccountms.models.dtos.AddOrRemovePrivilegesDTO;
import rw.companyz.useraccountms.models.dtos.CreateRoleDTO;
import rw.companyz.useraccountms.models.dtos.UpdateRoleDTO;
import rw.companyz.useraccountms.models.enums.EAttachStatus;
import rw.companyz.useraccountms.models.enums.EStatus;
import rw.companyz.useraccountms.services.IRoleService;
import rw.companyz.useraccountms.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController extends BaseController {


    private final IRoleService roleService;

    @Operation(summary = "Get all roles by status", description = "Privileges:  RETRIEVE_ROLE")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(value = "/status/{status}")
    public ResponseEntity<ApiResponse<List<Role>>> getAllByStatus(
            @PathVariable(value = "status") EStatus status) throws ResourceNotFoundException {
        List<Role> roles = this.roleService.getAllByStatus(status);
        return ResponseEntity.ok(
                new ApiResponse<>(roles, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get all paginated roles", description = "Privileges:  RETRIEVE_ROLE")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(value = "/paginated")
    public ResponseEntity<ApiResponse<Page<Role>>> getAllPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) throws ResourceNotFoundException {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<Role> roles = this.roleService.getAllPaginated(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(roles, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Search all roles", description = "Privileges:  RETRIEVE_ROLE")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(value = "/search")
    public ResponseEntity<ApiResponse<Page<Role>>> searchAll(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
           @NotNull @RequestParam(value = "q") String query,
            @RequestParam(value = "status", required = false) EStatus status,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) throws ResourceNotFoundException {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<Role> roles = this.roleService.searchAll(query, status, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(roles, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get a role by id", description = "Privileges:  RETRIEVE_ROLE")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(path="/{id}")
    public ResponseEntity<ApiResponse<Role>> getById(@Valid @PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        Role role = this.roleService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(role, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Get all privileges by attach status", description = "Privileges:  RETRIEVE_ROLE  \n Note: This API will return privilleges which are attached/unattached to a role")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(value = "/{id}/privileges/{attachStatus}")
    public ResponseEntity<ApiResponse<List<Privilege>>> getPrivileges(
            @Valid @PathVariable(value = "id") UUID id,
            @RequestParam(value = "q",required = false,defaultValue = "") String query,
            @Valid @PathVariable(value = "attachStatus") EAttachStatus attachStatus) throws ResourceNotFoundException {
        if (attachStatus == EAttachStatus.ATTACHED) {
            List<Privilege> privileges = this.roleService.getAllAttachedPrivileges(id,query);
            return ResponseEntity.ok(
                    new ApiResponse<>(privileges, localize("responses.getListSuccess"), HttpStatus.OK)
            );
        }
       else {
            List<Privilege> privileges = this.roleService.getAllNotAttachedPrivileges(id,query);
            return ResponseEntity.ok(
                    new ApiResponse<>(privileges, localize("responses.getListSuccess"), HttpStatus.OK)
            );
        }
    }

    @Operation(summary = "Create a role", description = "Privileges: any  of CREATE_ROLE, INITIATE_FLOW")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLE','INITIATE_FLOW')")
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@Valid @RequestBody CreateRoleDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
            Role role = this.roleService.create(dto);
            return new ResponseEntity<>(new ApiResponse<>(role, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Add privileges to a role", description = "Privileges: any  of GRANT_PRIVILEGES_TO_ROLE, INITIATE_FLOW")
    @PreAuthorize("hasAnyAuthority('GRANT_PRIVILEGES_TO_ROLE','INITIATE_FLOW')")
    @PutMapping("/{id}/addPrivileges")
    public ResponseEntity<ApiResponse<Role>> addPrivilegesToRole(@Valid @PathVariable(value = "id") UUID id, @Valid @RequestBody AddOrRemovePrivilegesDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        Role role = this.roleService.addPrivileges(id, dto);
        return new ResponseEntity<>(new ApiResponse<>(role, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove privileges from a role", description = "Privileges: any  of REVOKE_PRIVILEGES_FROM_ROLE, INITIATE_FLOW")
    @PreAuthorize("hasAnyAuthority('REVOKE_PRIVILEGES_FROM_ROLE','INITIATE_FLOW')")
    @PutMapping("/{id}/removePrivileges")
    public ResponseEntity<ApiResponse<Role>> removePrivilegesToRole(@Valid @PathVariable(value = "id") UUID id, @Valid @RequestBody AddOrRemovePrivilegesDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        Role role = this.roleService.removePrivileges(id, dto);
        return new ResponseEntity<>(new ApiResponse<>(role, localize("responses.saveEntitySuccess"), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a role", description = "Privileges:  UPDATE_ROLE")
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    @PutMapping(path="/{id}")
    public ResponseEntity<ApiResponse<Role>> updateById(@PathVariable(value = "id") UUID id, @Valid @RequestBody UpdateRoleDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
            Role role = this.roleService.updateById(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(role, localize("responses.updateEntitySuccess"), HttpStatus.OK));
    }


    @Operation(summary = "Get a role audits by role id", description = "Privileges:  RETRIEVE_ROLE")
    @PreAuthorize("hasAuthority('RETRIEVE_ROLE')" )
    @GetMapping(path="/{id}/audits")
    public ResponseEntity<ApiResponse<List<RoleAudit>>> getAuditsById(@Valid @PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        List<RoleAudit> audits = this.roleService.getAuditsById(id);
        return ResponseEntity.ok(new ApiResponse<>(audits, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Change status of a role", description = "Privileges:  ACTIVATE_OR_DISABLE_ROLE")
    @PreAuthorize("hasAuthority('ACTIVATE_OR_DISABLE_ROLE')")
    @PutMapping(value = "/{id}/status/{status}")
    public ResponseEntity<ApiResponse<Role>> changeStatus(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "status") EStatus status) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        Role role = this.roleService.changeStatusById(id, status);
        return ResponseEntity.ok(
                new ApiResponse<>(role, localize("responses.updateEntitySuccess"), HttpStatus.OK)
        );
    }


    @Operation(summary = "Delete a role", description = "Privileges:  DELETE_ROLE \n Note: only unused roles can be deleted")
    @PreAuthorize("hasAuthority('DELETE_ROLE')  ")
    @DeleteMapping(path="/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        this.roleService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(null, localize("responses.deleteEntitySuccess"), null, HttpStatus.OK));
    }


    @Override
    protected String getEntityName() {
        return "Role";
    }
}

