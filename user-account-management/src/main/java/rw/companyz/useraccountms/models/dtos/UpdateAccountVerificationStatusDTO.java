package rw.companyz.useraccountms.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.models.enums.EVerificationStatus;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Validated
public class UpdateAccountVerificationStatusDTO {
    @NotNull
    private UUID userId;
    @NotNull
    private EVerificationStatus verificationStatus;

    private String rejectionReason;
}
