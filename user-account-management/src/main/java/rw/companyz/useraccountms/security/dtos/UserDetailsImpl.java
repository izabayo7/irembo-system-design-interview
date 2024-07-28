package rw.companyz.useraccountms.security.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rw.companyz.useraccountms.models.*;
import rw.companyz.useraccountms.models.enums.EUserStatus;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private UUID id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String fullName;
    @NotNull
    private String emailAddress;
    @JsonIgnore
    private String password;

    @NotNull
    private String isOauthAccount = "N";

    private boolean deletedFlag;
    private String credentialsExpiryDate;

    private boolean isAccountExpired;

    private boolean isCredentialsExpired;

    private boolean isAccountEnabled;

    private boolean isAccountLocked;

    private Collection<GrantedAuthority> authorities;

    private EUserStatus status;

    private List<UserAccountRole> roles;

    public UserDetailsImpl(UUID id, String email, String password,
                           List<UserAccountRole> roles, String firstName, String lastName,
                           boolean isAccountEnabled, boolean isAccountExpired, boolean isAccountLocked,
                           boolean isCredentialsExpired, String credentialsExpiryDate,
                           EUserStatus status, Collection<GrantedAuthority> authorities ) {
        this.id = id;
        this.emailAddress = email;
        this.password = password;
        this.roles = roles;
        this.fullName =  firstName+ " "+lastName;
        this.firstName = firstName;
        this.lastName =  lastName;
        this.isAccountEnabled =  isAccountEnabled;
        this.isAccountExpired = isAccountExpired;
        this.isAccountLocked =  isAccountLocked;
        this.isCredentialsExpired = isCredentialsExpired;
        this.credentialsExpiryDate =  credentialsExpiryDate;
        this.status = status;
        this.authorities = authorities;

    }
    public static UserDetailsImpl build(UserAccount user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getRoles(),
                user.getFirstName(),
                user.getLastName(),
                user.isAccountEnabled(),
                user.isAccountExpired(),
                user.isAccountLocked(),
                user.isCredentialsExpired(),
                user.getCredentialsExpiryDate(),
                user.getStatus(), user.getAuthorities()
        );
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isAccountEnabled;
    }

    public UserAccount getUserAccount() {
        return UserAccount.builder()
                .id(id)
                .emailAddress(emailAddress)
                .password(password)
                .roles(roles)
                .firstName(firstName)
                .lastName(lastName)
                .isAccountEnabled(isAccountEnabled)
                .isAccountExpired(isAccountExpired)
                .isAccountLocked(isAccountLocked)
                .isCredentialsExpired(isCredentialsExpired)
                .credentialsExpiryDate(credentialsExpiryDate)
                .status(status)
                .build();
    }
}
