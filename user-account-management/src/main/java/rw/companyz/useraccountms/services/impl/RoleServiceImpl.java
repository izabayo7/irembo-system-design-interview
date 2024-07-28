package rw.companyz.useraccountms.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.companyz.useraccountms.exceptions.BadRequestAlertException;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.UserAccountRole;
import rw.companyz.useraccountms.models.audits.RoleAudit;
import rw.companyz.useraccountms.models.dtos.AddOrRemovePrivilegesDTO;
import rw.companyz.useraccountms.models.dtos.CreateRoleDTO;
import rw.companyz.useraccountms.models.dtos.UpdateRoleDTO;
import rw.companyz.useraccountms.models.enums.EAuditType;
import rw.companyz.useraccountms.models.enums.EStatus;
import rw.companyz.useraccountms.repositories.IRoleAuditRepository;
import rw.companyz.useraccountms.repositories.IRoleRepository;
import rw.companyz.useraccountms.repositories.IUserRepository;
import rw.companyz.useraccountms.repositories.IUserRoleRepository;
import rw.companyz.useraccountms.security.dtos.CustomUserDTO;
import rw.companyz.useraccountms.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository roleRepository;
    private final IRoleAuditRepository roleAuditRepository;
    private final IUserRepository userRepository;
    private final IPrivilegeService privilegeService;
    private final IRolePrivilegeService rolePrivilegeService;

    private final IUserRoleRepository userRoleRepository;

    private final IUserRoleService userRoleService;

    private final IJwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RoleServiceImpl(IRoleRepository roleRepository, IRoleAuditRepository roleAuditRepository, IUserRepository userRepository, IPrivilegeService privilegeService, IRolePrivilegeService rolePrivilegeService, IUserRoleRepository userRoleRepository, @Lazy IUserRoleService userRoleService, IJwtService jwtService) {
        this.roleRepository = roleRepository;
        this.roleAuditRepository = roleAuditRepository;
        this.userRepository = userRepository;
        this.privilegeService = privilegeService;
        this.rolePrivilegeService = rolePrivilegeService;
        this.userRoleRepository = userRoleRepository;
        this.userRoleService = userRoleService;
        this.jwtService = jwtService;
    }

    @Override
    public Page<Role> getAllPaginated(Pageable pageable) throws ResourceNotFoundException {
        Page<Role> roles =  this.roleRepository.findAllByStatusNot(EStatus.DELETED
                , pageable);
        List<Role> roleList = getRoles(roles.stream().toList());
        Page<Role> entityList = new PageImpl<Role>(roleList, pageable,
                roles.getTotalElements());
        return entityList;
    }

    private List<Role> getRoles(List<Role> roles) throws ResourceNotFoundException {
        List<Role> roleList =  new ArrayList<>();
        for (Role role: roles){
            role.setPrivileges(this.rolePrivilegeService.getAllByRole(role));
            roleList.add(role);
        }
        return roleList;
    }

    @Override
    public List<Role> getAllByStatus(EStatus status) throws ResourceNotFoundException {

        return getRoles(this.roleRepository.findAllByStatus(status));
    }

    @Override
    public List<Privilege> getAllAttachedPrivileges(UUID roleId, String query) throws ResourceNotFoundException {
        Role role = this.roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", roleId.toString()));
        return this.rolePrivilegeService.getAllAttachedPrivilegesByRole(role,query);
    }

    @Override
    public List<Privilege> getAllNotAttachedPrivileges(UUID roleId, String query) throws ResourceNotFoundException {
        Role role = this.getPureRole(roleId);
        return this.rolePrivilegeService.getAllUnattachedPrivilegesByRole(role, query);
    }

    @Override
    public Page<Role> searchAll(String q, EStatus status, Pageable pageable) throws ResourceNotFoundException {
        return roleRepository.searchAll(q, status, pageable);
    }


    @Override
    public Role create(CreateRoleDTO dto) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        var roleExistsByName = roleRepository.findByName(dto.getName().toUpperCase());
        if(roleExistsByName.isPresent()){
            throw new BadRequestException("Role with Name '" + dto.getName() + "' exists");
        }

        var role  = new Role(dto);
        this.roleRepository.save(role);
        role.setPrivileges(this.rolePrivilegeService.create(role,dto.getRemarks(), dto.getPrivilegeIds()));

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        RoleAudit audit = new RoleAudit(role, EAuditType.CREATE, userDTO.getId(), userDTO.getFullNames(), "CREATE_ROLE", dto.getRemarks(), null);
        this.roleAuditRepository.save(audit);
        return role;
    }

    @Override
    public Role getById(UUID id) throws ResourceNotFoundException {
        return getRole(id);
    }

    public List<RoleAudit> getAuditsById(UUID id) throws ResourceNotFoundException {
        Role role = this.getById(id);
        return this.roleAuditRepository.findAllByRole(role);
    }

    @Override
    public Role getPureRole(UUID id) throws ResourceNotFoundException {
        return this.roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id.toString()));
    }


    private Role getRole(UUID id) throws ResourceNotFoundException {
        Role role = this.getPureRole(id);
        role.setPrivileges(this.rolePrivilegeService.getAllByRole(role));
        return role;
    }

    @Override
    public Role addPrivileges(UUID id, AddOrRemovePrivilegesDTO dto) throws ResourceNotFoundException, DuplicateRecordException {
        Role role = this.getById(id);
        List<UUID> privileges = new ArrayList<>();

        for (String privilege: dto.getPrivileges()) {
            Privilege entity = privilegeService.getByName(privilege);
            boolean exists = role.getPrivileges().stream().anyMatch(obj -> Objects.equals(obj.getName(), entity.getName()));
            if (!exists) privileges.add(entity.getId());
        }

        if (!privileges.isEmpty()) {

            this.rolePrivilegeService.create(role,dto.getRemarks(), privileges);

            Role modifiedRole = getById(id);

            CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
            RoleAudit audit = new RoleAudit(modifiedRole, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "GRANT_PRIVILEGES_TO_ROLE", dto.getRemarks(), null);
            this.roleAuditRepository.save(audit);

            return modifiedRole;
        }
        return this.getById(id);
    }

    @Override
    @Transactional
    public Role removePrivileges(UUID id, AddOrRemovePrivilegesDTO dto) throws ResourceNotFoundException {
        Role role = this.getById(id);

        for (String privilegeName : dto.getPrivileges()) {
            Privilege privilegeToRemove = privilegeService.getByName(privilegeName);

            boolean exists = role.getPrivileges().removeIf(p -> p.getName().equals(privilegeToRemove.getName()));

            if (exists) this.rolePrivilegeService.deleteById(role.getId(), privilegeToRemove.getId());
        }

        Role modifiedRole = getById(id);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        RoleAudit audit = new RoleAudit(modifiedRole, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "REVOKE_PRIVILEGES_FROM_ROLE", dto.getRemarks(), null);
        this.roleAuditRepository.save(audit);

        return modifiedRole;
    }

    @Override
    public Role updateById(UUID id, UpdateRoleDTO dto) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        Role role = this.roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id.toString())
        );

        if(!dto.getName().equals(role.getName())){
            if(this.roleRepository.findByName(dto.getName()).isPresent()) throw new BadRequestException("Role with Name '" + dto.getName() + "' exists");
        }

        role.setName(dto.getName().toUpperCase());
        role.setDescription(dto.getDescription());
        this.roleRepository.save(role);

        Role modifiedRole = getById(id);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        RoleAudit audit = new RoleAudit(modifiedRole, EAuditType.UPDATE, userDTO.getId(), userDTO.getFullNames(), "UPDATE_ROLE", dto.getRemarks(), null);
        this.roleAuditRepository.save(audit);

        return modifiedRole;
    }

    @Override
    public Role changeStatusById(UUID id, EStatus status) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {
        Role role = this.getById(id);

        if (status == EStatus.DELETED) { throw new  BadRequestAlertException("Status not allowed");}
        if(status.equals(EStatus.INACTIVE)){
            List<UserAccountRole> userAccountRoles = userRoleRepository.findByRole(role);
            for(UserAccountRole userAccountRole : userAccountRoles){
                userRoleService.remove(userAccountRole.getId());
            }
        }
        role.setStatus(status);
        this.roleRepository.save(role);

        CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
        RoleAudit audit = new RoleAudit(role, (status == EStatus.ACTIVE) ? EAuditType.ACTIVATE : EAuditType.DISABLE, userDTO.getId(), userDTO.getFullNames(), "ACTIVATE_OR_DISABLE_ROLE", "Role status changed", null);
        this.roleAuditRepository.save(audit);

        return role;
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException {
        Role role = this.roleRepository.findById(id)  .orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id.toString())
        );

        List<UserAccount> userAccounts = this.userRepository.findAllByRole(role);
        if (userAccounts.size() > 0) {
            role.setStatus(EStatus.DELETED);
            this.roleRepository.save(role);

            Role modifiedRole = getById(id);
            CustomUserDTO userDTO = this.jwtService.extractLoggedInUser();
            RoleAudit audit = new RoleAudit(modifiedRole, EAuditType.DELETE, userDTO.getId(), userDTO.getFullNames(), "DELETE_ROLE", "Role status set to DELETED", null);
            this.roleAuditRepository.save(audit);
        } else {
            this.roleRepository.deleteById(id);
        }
    }

}
