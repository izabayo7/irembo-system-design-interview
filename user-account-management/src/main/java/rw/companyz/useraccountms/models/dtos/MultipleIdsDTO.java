package rw.companyz.useraccountms.models.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MultipleIdsDTO {

    @NotNull
    List<UUID> ids;
}
