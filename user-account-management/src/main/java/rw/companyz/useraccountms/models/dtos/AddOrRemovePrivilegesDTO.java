package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AddOrRemovePrivilegesDTO extends FileAndRemarkDTO{
    @NotNull
    private List<String> privileges;
}
