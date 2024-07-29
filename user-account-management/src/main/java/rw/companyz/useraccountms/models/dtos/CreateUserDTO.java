package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import rw.companyz.useraccountms.models.enums.EGender;
import rw.companyz.useraccountms.models.enums.EMaritalStatus;
import rw.companyz.useraccountms.security.ValidPassword;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CreateUserDTO {
    @NotBlank
    @Length(min = 2)
    private String firstName;

    @NotBlank
    @Length(min = 2)
    private String lastName;

    @NotNull
    private EGender gender;

    @Email
    @NotNull
    private String emailAddress;

    @NotNull
    private EMaritalStatus maritalStatus;

    @NotNull
    private String nationality;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    @ValidPassword
    private String password;

    private List<CreateUserRoleDTO> roleIds = new ArrayList<>();

}
