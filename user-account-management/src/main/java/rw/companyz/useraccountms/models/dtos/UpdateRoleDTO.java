package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleDTO extends FileAndRemarkDTO{
    @NotBlank
    private String name;
    private String description;
}
