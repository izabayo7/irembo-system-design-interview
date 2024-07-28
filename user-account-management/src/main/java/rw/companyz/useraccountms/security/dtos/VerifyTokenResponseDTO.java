package rw.companyz.useraccountms.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.companyz.useraccountms.models.UserAccount;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTokenResponseDTO{

    private UserAccount userAccount;

    private JwtAuthenticationResponse token;
}
