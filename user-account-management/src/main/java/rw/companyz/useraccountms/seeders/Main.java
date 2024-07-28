package rw.companyz.useraccountms.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rw.companyz.useraccountms.models.*;
import rw.companyz.useraccountms.models.dtos.CreateUserDTO;
import rw.companyz.useraccountms.models.dtos.CreateUserRoleDTO;
import rw.companyz.useraccountms.models.enums.EGender;
import rw.companyz.useraccountms.models.enums.EMaritalStatus;
import rw.companyz.useraccountms.models.enums.EStatus;
import rw.companyz.useraccountms.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Main {
    @Bean
    CommandLineRunner commandLineRunner(IRoleRepository roleRepo,
                                        IPrivilegeRepository privRep,
                                        IRolePrivilegeRepository rolePrivRepo,
                                        IUserRepository userRepo,
                                        IUserRoleRepository userRole
    ) {
        return args -> {

            if (!privRep.findByName("INITIATE_FLOW").isPresent()) {

                Privilege priv = new Privilege("INITIATE_FLOW", ""); // TODO: create all necesary ones here

                privRep.save(priv);

                Role role = new Role("SUPERADMIN", "SuperAdmin Role");

                roleRepo.save(role);

                RolePrivilege rolePrivilege = RolePrivilege.builder()
                        .privilege(priv)
                        .role(role)
                        .build();

                // TODO: also create the default role for signups

                rolePrivRepo.save(rolePrivilege);

                List<Role> roles = new ArrayList<>();
                roles.add(role);

                CreateUserDTO userDto = new CreateUserDTO("Cedric", "Izabayo", EGender.MALE, "cedricizabayo7@gmail.com", EMaritalStatus.SINGLE, "RWANDA", LocalDate.of(2002,12,17), "", new ArrayList<CreateUserRoleDTO>());

                UserAccount userAccount = new UserAccount(userDto);

                userAccount.setPassword("$2a$10$j8cdEI660IPMDaJSVMB5P.mhm1.tHyVg7RxUNVJDcyJKsc7sJxHna");
                userAccount.setCredentialsExpired(false);
                userAccount.setCredentialsExpiryDate(LocalDateTime.now(ZoneId.of("Africa/Kigali")).plusMonths(12).toString());
                userAccount.setAccountEnabled(true);
                userAccount.setAccountExpired(false);
                userAccount.setAccountLocked(false);
                userAccount.setDeletedFlag(false);

                userRepo.save(userAccount);

                UserAccountRole userAccountrole = UserAccountRole.builder()
                        .role(role)
                        .user(userAccount)
                        .status(EStatus.ACTIVE)
                        .build();

                userRole.save(userAccountrole);
            }
        };


    }
}