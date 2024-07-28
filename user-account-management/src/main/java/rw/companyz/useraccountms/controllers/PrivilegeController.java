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
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.domains.ApiResponse;
import rw.companyz.useraccountms.models.dtos.CreatePrivilegeDTO;
import rw.companyz.useraccountms.models.dtos.DeletePrivilegeDTO;
import rw.companyz.useraccountms.services.IPrivilegeService;
import rw.companyz.useraccountms.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/privileges")
@RequiredArgsConstructor
public class PrivilegeController extends BaseController{


    private final IPrivilegeService privilegeService;

    @Operation(summary = "Get all privileges", description = "Privileges:  RETRIEVE_PRIVILEGE")
    @PreAuthorize("hasAuthority('RETRIEVE_PRIVILEGE')")
    @GetMapping(value = "")
    public ResponseEntity<ApiResponse<List<Privilege>>> getAll()
    {
        List<Privilege> privileges = this.privilegeService.getAll();
        return ResponseEntity.ok(
                new ApiResponse<>(privileges, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }


    @Operation(summary = "Get all paginated privileges", description = "Privileges:  RETRIEVE_PRIVILEGE")
    @PreAuthorize("hasAuthority('RETRIEVE_PRIVILEGE')")
    @GetMapping(value = "/paginated")
    public ResponseEntity<ApiResponse<Page<Privilege>>> getAllPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit)
    {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<Privilege> privileges = this.privilegeService.getAllPaginated(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(privileges, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Search all paginated privileges", description = "Privileges:  RETRIEVE_PRIVILEGE")
    @PreAuthorize("hasAuthority('RETRIEVE_PRIVILEGE')")
    @GetMapping(value = "/search")
    public ResponseEntity<ApiResponse<Page<Privilege>>> searchAll(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @NotNull @RequestParam(value = "q") String query,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit)
    {
        Pageable pageable = (Pageable) PageRequest.of(page-1, limit, Sort.Direction.DESC,"id");
        Page<Privilege> privileges = this.privilegeService.searchAll(query, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(privileges, localize("responses.getListSuccess"), HttpStatus.OK)
        );
    }

    @Operation(summary = "Get a privilege by id", description = "Privileges:  RETRIEVE_PRIVILEGE")
    @PreAuthorize("hasAuthority('RETRIEVE_PRIVILEGE')")
    @GetMapping(path="/{id}")
    public ResponseEntity<ApiResponse<Privilege>> getById(@Valid @PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        Privilege privilege = this.privilegeService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(privilege, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Get a privilege by name", description = "Privileges:  RETRIEVE_PRIVILEGE")
    @PreAuthorize("hasAuthority('RETRIEVE_PRIVILEGE')")
    @GetMapping(path="/name/{name}")
    public ResponseEntity<ApiResponse<Privilege>> getByName(@Valid @PathVariable(value = "name") String name) throws ResourceNotFoundException {
        Privilege privilege = this.privilegeService.getByName(name);
        return ResponseEntity.ok(new ApiResponse<>(privilege, localize("responses.getEntitySuccess"), HttpStatus.OK));
    }

    @Operation(summary = "Create a privilege", description = "Privileges:  INITIATE_FLOW")
    @PreAuthorize("hasAuthority('INITIATE_FLOW')")
    @PostMapping
    public ResponseEntity<Privilege> saveOperation(@RequestBody CreatePrivilegeDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        return ResponseEntity.ok(privilegeService.create(dto));
    }

    @Operation(summary = "Delete multiple privileges", description = "Privileges:  INITIATE_FLOW")
     @PreAuthorize("hasAuthority('INITIATE_FLOW')")
    @PostMapping("/deleteMany")
    public ResponseEntity<String> deleteMany(@RequestBody DeletePrivilegeDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
       privilegeService.delete(dto);
        return  ResponseEntity.ok("Deleted");
    }

        @Override
        protected String getEntityName() {
            return "Privilege";
        }
}

