package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import rw.companyz.useraccountms.models.enums.EGender;
import rw.companyz.useraccountms.models.enums.EMaritalStatus;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private EGender gender;

    @NotNull
    private EMaritalStatus maritalStatus;

    @NotNull
    private String nationality;

    @NotNull
    private LocalDate dateOfBirth;

}
