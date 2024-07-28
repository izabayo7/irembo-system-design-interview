package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMultipleUserRoleDTO {
    @NotNull
    private UUID roleId;

    @NotNull
    private List<UUID> userIds;


    public CreateUserRoleDTO toCreateUserRoleDTO() {
        return new CreateUserRoleDTO(roleId);
    }
}
