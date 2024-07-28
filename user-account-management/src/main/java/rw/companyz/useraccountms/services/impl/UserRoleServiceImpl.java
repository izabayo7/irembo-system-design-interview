package rw.companyz.useraccountms.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.companyz.useraccountms.exceptions.BadRequestAlertException;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Role;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.UserAccountRole;
import rw.companyz.useraccountms.models.dtos.CreateUserRoleDTO;
import rw.companyz.useraccountms.models.enums.EStatus;
import rw.companyz.useraccountms.repositories.IUserRoleRepository;
import rw.companyz.useraccountms.services.IRoleService;
import rw.companyz.useraccountms.services.IUserRoleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserRoleServiceImpl implements IUserRoleService {

    private final IUserRoleRepository userRoleRepository;
    private final IRoleService roleService;

    @Override
    public UserAccountRole create(CreateUserRoleDTO dto, UserAccount user) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        Role role = roleService.getPureRole(dto.getRoleId());

        Optional<UserAccountRole> optional = this.userRoleRepository.findByUserAndRole(user, role);

        if (optional.isPresent() && optional.get().getStatus() == EStatus.ACTIVE) throw new DuplicateRecordException("UserAccountRole", "role", role.getName());

        if (optional.isPresent() && optional.get().getStatus() == EStatus.INACTIVE) {
            UserAccountRole userAccountRole = optional.get();
            userAccountRole.setStatus(EStatus.ACTIVE);
            return this.userRoleRepository.save(userAccountRole);
        }

        var userRole= UserAccountRole.builder()
                .role(role)
                .user(user)
                .status(EStatus.ACTIVE)
                .build();
        var savedUserAccountRole = this.userRoleRepository.save(userRole);
        return savedUserAccountRole;
    }

    @Override
    public UserAccountRole remove(UUID roleAccountId) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException  {

        UserAccountRole userAccountRole = this.getById(roleAccountId);
        userAccountRole.setStatus(EStatus.INACTIVE);

        var savedUserAccountRole =  this.userRoleRepository.save(userAccountRole);
        return savedUserAccountRole;
    }

    @Override
    public void createAll(List<CreateUserRoleDTO> dtos,UserAccount user) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        for (CreateUserRoleDTO dto: dtos){
            Role role = roleService.getPureRole(dto.getRoleId());
            var userRole  =   UserAccountRole.builder()
                    .role(role)
                    .user(user)
                    .status(EStatus.ACTIVE)
                    .build();
            this.userRoleRepository.save(userRole);
        }
    }


    @Override
    public List<UserAccountRole> getAllByUserId(UserAccount user) throws ResourceNotFoundException {
        return userRoleRepository.findAllByUserAndStatus(user,EStatus.ACTIVE);
    }

    @Override
    public List<UserAccountRole> getAllActiveByUserId(UserAccount user) throws ResourceNotFoundException {
        return userRoleRepository.findAllActiveByUser(user);
    }


    @Override
    public UserAccountRole getById(UUID id) throws ResourceNotFoundException {
        return userRoleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UserAccountRole", "id", id.toString())
        );
    }

    @Override
    public UserAccountRole changeStatus(UUID id, EStatus status) throws ResourceNotFoundException, DuplicateRecordException, BadRequestException {

        UserAccountRole userRole= userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAccountRole", "id", id.toString()));
        userRole.setStatus(status);
        var savedUserAccountRole = this.userRoleRepository.save(userRole);
        return savedUserAccountRole;
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException, DuplicateRecordException, BadRequestAlertException, BadRequestException {
        UserAccountRole userRole= userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAccountRole", "id", id.toString()));

        userRole.setStatus(EStatus.DELETED);
        var userAccountRole =  this.userRoleRepository.save(userRole);
    }
}
