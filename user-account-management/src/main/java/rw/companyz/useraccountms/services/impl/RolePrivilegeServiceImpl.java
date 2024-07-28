package rw.companyz.useraccountms.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.RolePrivilege;
import rw.companyz.useraccountms.repositories.IPrivilegeRepository;
import rw.companyz.useraccountms.repositories.IRolePrivilegeRepository;
import rw.companyz.useraccountms.repositories.IRoleRepository;
import rw.companyz.useraccountms.services.IRolePrivilegeService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePrivilegeServiceImpl implements IRolePrivilegeService {

    private final IRolePrivilegeRepository rolePrivilegeRepository;

    private final IPrivilegeRepository privilegeRepository;

    private final IRoleRepository roleRepository;

    private final JwtServiceImpl jwtService;

    @Override
    public List<Privilege> create(Role role, String remarks, List<UUID> privilegeIds) throws DuplicateRecordException, ResourceNotFoundException {
        List<Privilege> privileges = new ArrayList<>();
        for(UUID privilegeId: privilegeIds){
            Privilege privilege =  privilegeRepository.findById(privilegeId).get();
            var rolePrivilege =  RolePrivilege.builder()
                    .privilege(privilege)
                    .role(role)
                    .build();
            rolePrivilegeRepository.save(rolePrivilege);
            privileges.add(privilege);
        }

        return privileges;
    }

    @Override
    public List<Privilege> getAllByRole(Role role) throws ResourceNotFoundException {
        List<RolePrivilege> rolePrivileges = rolePrivilegeRepository.findAllByRole(role);
        List<Privilege> privileges =  new ArrayList<>();
        for(RolePrivilege rolePrivilege: rolePrivileges){
            privileges.add(rolePrivilege.getPrivilege());
        }
        return privileges;
    }

    @Override
    public List<Privilege> getAllAttachedPrivilegesByRole(Role role, String query) throws ResourceNotFoundException {
        List<RolePrivilege> rolePrivileges = rolePrivilegeRepository.searchAllAttachedPrivilegesByRole(role,query);
        List<Privilege> privileges =  new ArrayList<>();
        for(RolePrivilege rolePrivilege: rolePrivileges){
            privileges.add(rolePrivilege.getPrivilege());
        }
        return privileges;
    }

    @Override
    public void deleteById(UUID roleId, UUID privilegeId) throws ResourceNotFoundException {
        Privilege privilege =  privilegeRepository.findById(privilegeId).get();
        Role role  =  roleRepository.findById(roleId).get();
        rolePrivilegeRepository.deleteByRoleAndPrivilege(role, privilege);
    }

    @Override
    public List<Privilege> getAllUnattachedPrivilegesByRole(Role role, String query) throws ResourceNotFoundException {
        return this.rolePrivilegeRepository.findAllUnattachedPrivilegesByRole(role, query);
    }
}
