package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateRoleDTO extends FileAndRemarkDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private List<UUID> privilegeIds;
}
