package rw.companyz.useraccountms.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileAndRemarkDTO {
    @NotEmpty
    private String remarks;

    private UUID fileId;

}
