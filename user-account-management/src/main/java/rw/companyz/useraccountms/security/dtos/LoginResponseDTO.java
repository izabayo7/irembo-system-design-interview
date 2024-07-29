package rw.companyz.useraccountms.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
	private boolean requiresMfa = false;
	private String message;
	private JwtAuthenticationResponse token;

}
