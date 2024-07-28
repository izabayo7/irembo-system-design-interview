package rw.companyz.useraccountms.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.UserAccountRole;
import rw.companyz.useraccountms.repositories.IUserRepository;
import rw.companyz.useraccountms.security.dtos.UserDetailsImpl;
import rw.companyz.useraccountms.services.IRolePrivilegeService;
import rw.companyz.useraccountms.services.IUserRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    private final IUserRoleService userRoleReService;

    private  final IRolePrivilegeService rolePrivilegeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<UserAccount> user = userRepository.findByEmailAddress(username);

        if(user.isPresent())   {
            UserAccount userAccount =  user.get();
            try {
                List<UserAccountRole> userRoles = userRoleReService.getAllByUserId(user.get());
                List<Privilege> privileges = new ArrayList<>();

                for(UserAccountRole userAccountRole: userRoles){
                    privileges.addAll(this.rolePrivilegeService.getAllByRole(userAccountRole.getRole()));
                }
                userAccount.setRoles(userRoles);

                List<GrantedAuthority> authorities = new ArrayList<>();

                for (Privilege privilege: privileges) {
                    authorities.add(new SimpleGrantedAuthority(privilege.getAuthority()));
                }
                userAccount.setAuthorities(authorities);
                return  UserDetailsImpl.build(userAccount);
            } catch (ResourceNotFoundException e) {
                throw new UsernameNotFoundException("User account has no roles");
            }

        } else{
            throw new UsernameNotFoundException("User not found");
        }
    }

}
