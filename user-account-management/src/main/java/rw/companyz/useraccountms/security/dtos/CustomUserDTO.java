package rw.companyz.useraccountms.security.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.enums.EUserStatus;

import java.util.UUID;

@Data
@Getter
@NoArgsConstructor
public class CustomUserDTO {
    private String fullNames;
    private UUID id;
    private String emailAddress;
    private EUserStatus status;

    public CustomUserDTO(UserAccount userAccount) {
        this.id = userAccount.getId();
        this.emailAddress = userAccount.getEmailAddress();
        this.status = userAccount.getStatus();
        this.fullNames = userAccount.getFullName();
    }
}
