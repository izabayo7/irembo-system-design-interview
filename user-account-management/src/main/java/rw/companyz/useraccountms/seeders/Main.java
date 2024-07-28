package rw.companyz.useraccountms.seeders;

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
import java.util.Arrays;
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
            if (privRep.count() == 0) {  // Only seed if no privileges exist
                // Define all privileges
                List<Privilege> privileges = Arrays.asList(
                        new Privilege("INITIATE_FLOW", "This is a default privilege that provides the user with an access to add privileges to a role, creating a user, assigning privilege to user, and creating a role."),
                        new Privilege("REVOKE_ROLE_FROM_USER", "This is a privilege that provides the user with ability to remove a role from another user."),
                        new Privilege("UNLOCK_USER", "This is a privilege that provides the user with ability to unlock a user after their account have been locked."),
                        new Privilege("RETRIEVE_PRIVILEGE", "This is a privilege that allows a user to view privileges in the system."),
                        new Privilege("RETRIEVE_ROLE", "This is a privilege that allows a user to view roles in the system."),
                        new Privilege("CREATE_ROLE", "This is a privilege that allows a user to create a role in the system."),
                        new Privilege("UPDATE_ROLE", "This is a privilege that allows a user to update role details in the system."),
                        new Privilege("ACTIVATE_OR_DISABLE_ROLE", "This is a privilege that allows a user to activate or deactivate roles in the system."),
                        new Privilege("DELETE_ROLE", "This is a privilege that allows a user to delete roles in the system."),
                        new Privilege("GRANT_PRIVILEGES_TO_ROLE", "This is a privilege that allows a user to add privileges to a role in the system."),
                        new Privilege("REVOKE_PRIVILEGES_FROM_ROLE", "This is a privilege that allows a user to remove privileges from a role in the system."),
                        new Privilege("RETRIEVE_USER", "This is a privilege that allows a user to view users in the system."),
                        new Privilege("CREATE_USER", "This is a privilege that allows a user to create users in the system."),
                        new Privilege("UPDATE_USER", "This is a privilege that allows a user to update user details in the system."),
                        new Privilege("ACTIVATE_OR_DISABLE_USER", "This is a privilege that allows a user to activate or deactivate users in the system."),
                        new Privilege("DELETE_USER", "This is a privilege that allows a user to delete users in the system."),
                        new Privilege("ASSIGN_ROLE_TO_USER", "This is a privilege that allows a user to assign roles to users in the system."),
                        new Privilege("RESET_PASSWORD", "This is a privilege that allows a user to reset user's password in the system."),
                        new Privilege("UPDATE_ACCOUNT_VERIFICATION", "This is a privilege that allows a user to verify/deny users account verification submissions."),
                        new Privilege("RESET_ACCOUNT_VERIFICATION", "This is a privilege that allows a user to reset users account verification status, incase there was a mistake in the verification decision.")
                );

                privRep.saveAll(privileges);

                // Create roles
                Role superAdminRole = new Role("SUPERADMIN", "Creating privileges, roles and users");
                Role adminRole = new Role("ADMIN", "Viewing all users, resetting verification status when necessary");
                Role approverRole = new Role("APPROVER", "Viewing pending users, approving them");

                // Save roles
                roleRepo.saveAll(Arrays.asList(superAdminRole, adminRole, approverRole));

                // Assign privileges to roles
                // SUPERADMIN gets all privileges
                for (Privilege priv : privileges) {
                    RolePrivilege rolePrivilege = RolePrivilege.builder()
                            .privilege(priv)
                            .role(superAdminRole)
                            .build();
                    rolePrivRepo.save(rolePrivilege);
                }

                // ADMIN gets specific privileges
                List<String> adminPrivileges = Arrays.asList("RETRIEVE_USER", "RESET_ACCOUNT_VERIFICATION", "UPDATE_ACCOUNT_VERIFICATION");
                for (String privName : adminPrivileges) {
                    Privilege priv = privRep.findByName(privName).orElseThrow(() -> new RuntimeException("Privilege not found: " + privName));
                    RolePrivilege rolePrivilege = RolePrivilege.builder()
                            .privilege(priv)
                            .role(adminRole)
                            .build();
                    rolePrivRepo.save(rolePrivilege);
                }

                // APPROVER gets specific privileges
                List<String> approverPrivileges = Arrays.asList("RETRIEVE_USER", "UPDATE_ACCOUNT_VERIFICATION");
                for (String privName : approverPrivileges) {
                    Privilege priv = privRep.findByName(privName).orElseThrow(() -> new RuntimeException("Privilege not found: " + privName));
                    RolePrivilege rolePrivilege = RolePrivilege.builder()
                            .privilege(priv)
                            .role(approverRole)
                            .build();
                    rolePrivRepo.save(rolePrivilege);
                }

                // Create default SUPERADMIN user
                CreateUserDTO userDto = new CreateUserDTO("Cedric", "Izabayo", EGender.MALE, "cedricizabayo7@gmail.com", EMaritalStatus.SINGLE, "RWANDA", LocalDate.of(2002,12,17), "", new ArrayList<CreateUserRoleDTO>());

                UserAccount userAccount = new UserAccount(userDto);

                userAccount.setPassword("$2a$10$j8cdEI660IPMDaJSVMB5P.mhm1.tHyVg7RxUNVJDcyJKsc7sJxHna"); // Irembo@2024
                userAccount.setCredentialsExpired(false);
                userAccount.setCredentialsExpiryDate(LocalDateTime.now(ZoneId.of("Africa/Kigali")).plusMonths(3).toString());
                userAccount.setAccountEnabled(true);
                userAccount.setAccountExpired(false);
                userAccount.setAccountLocked(false);
                userAccount.setDeletedFlag(false);

                userRepo.save(userAccount);

                UserAccountRole userAccountrole = UserAccountRole.builder()
                        .role(superAdminRole)
                        .user(userAccount)
                        .status(EStatus.ACTIVE)
                        .build();

                userRole.save(userAccountrole);
            }
        };

    }
}