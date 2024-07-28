package rw.companyz.useraccountms.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import rw.companyz.useraccountms.fileHandling.File;
import rw.companyz.useraccountms.models.dtos.CreateUserDTO;
import rw.companyz.useraccountms.models.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(AuditingEntityListener.class)
public class UserAccount extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "nationality")
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private EGender gender;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profile_picture_id")
    private File profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private EMaritalStatus maritalStatus;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EUserStatus status = EUserStatus.PENDING;

    // AccountVerification

    @Column(name = "nid_or_passport")
    private String nidOrPassport;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "official_document_id")
    private File officialDocument;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<UserAccountRole> roles;

    @JsonIgnore
    @Transient
    private Collection<GrantedAuthority> authorities;

    @JsonIgnore
    private String fullName;

    @JsonIgnore
    private Integer age;

    private boolean deletedFlag;

    private String credentialsExpiryDate;

    private boolean isAccountExpired;

    private boolean isCredentialsExpired;

    private boolean isAccountEnabled;

    private boolean isAccountLocked;

    @JsonIgnore
    private String otp;

    @JsonIgnore
    @Column
    private String authToken;

    @JsonIgnore
    @Column
    private LocalDateTime authTokenExpiryDate;

    @JsonIgnore
    private LocalDateTime otpExpiryDate;

    @Enumerated(EnumType.STRING)
    private EOTPStatus otpStatus;

    @Column(name = "last_login")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    @JsonIgnore
    @Column(unique = true)
    private UUID sessionId;

    @Column
    @Enumerated(EnumType.STRING)
    private ELoginStatus loginStatus = ELoginStatus.INACTIVE;

    public UserAccount(CreateUserDTO dto) {
        this.emailAddress = dto.getEmailAddress();
        this.nationality = dto.getNationality();
        this.dateOfBirth = dto.getDateOfBirth();
        this.maritalStatus = dto.getMaritalStatus();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.gender = dto.getGender();
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public Integer getAge() {
        return LocalDate.now().getYear() - this.dateOfBirth.getYear();
    }
}
