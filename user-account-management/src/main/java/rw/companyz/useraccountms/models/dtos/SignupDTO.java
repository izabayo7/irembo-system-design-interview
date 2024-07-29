package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Validated
public class SignupDTO extends CreateUserDTO {
    @NotNull
    private MultipartFile file;

    public CreateUserDTO toCreateUserDTO() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setFirstName(this.getFirstName());
        createUserDTO.setLastName(this.getLastName());
        createUserDTO.setGender(this.getGender());
        createUserDTO.setEmailAddress(this.getEmailAddress());
        createUserDTO.setMaritalStatus(this.getMaritalStatus());
        createUserDTO.setNationality(this.getNationality());
        createUserDTO.setDateOfBirth(this.getDateOfBirth());
        createUserDTO.setPassword(this.getPassword());
        createUserDTO.setRoleIds(this.getRoleIds());
        return createUserDTO;
    }
}
