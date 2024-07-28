package rw.companyz.useraccountms.models.audits.embeddables;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.enums.EGender;
import rw.companyz.useraccountms.models.enums.EMaritalStatus;
import rw.companyz.useraccountms.models.enums.EUserStatus;

import java.time.LocalDate;
import java.util.UUID;

@Embeddable
@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class UserEmbeddable {
    @Column(name = "_first_name")
    private String firstName;

    @Column(name = "_last_name")
    private String lastName;

    @Column(name="_email_address")
    private String emailAddress;

    @Column(name = "_nationality")
    private String nationality;

    @Column(name = "_date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "_marital_status")
    private EMaritalStatus maritalStatus;

    @Column(name = "_nid_or_passport")
    private String nidOrPassport;

    @Column(name = "_official_document_id")
    private UUID officialDocumentId;

    @Column(name = "_gender")
    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "_roleIds")
    private String roleIds;

    @Column(name = "_profilePictureId")
    private UUID profilePictureId;

    @Column(name = "_status")
    @Enumerated(EnumType.STRING)
    private EUserStatus status;

    public UserEmbeddable(@NotNull UserAccount userAccount) {
        this.firstName = userAccount.getFirstName();
        this.lastName = userAccount.getLastName();
        this.gender = userAccount.getGender();
        this.emailAddress = userAccount.getEmailAddress();
        this.dateOfBirth = userAccount.getDateOfBirth();
        this.maritalStatus = userAccount.getMaritalStatus();
        this.nationality = userAccount.getNationality();
        this.nidOrPassport = userAccount.getNidOrPassport();
        this.status = userAccount.getStatus();

        if (userAccount.getOfficialDocument() != null)
            this.officialDocumentId = userAccount.getOfficialDocument().getId();
        if (userAccount.getProfilePicture() != null)
            this.profilePictureId = userAccount.getProfilePicture().getId();
    }
}

