package rw.companyz.useraccountms.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
	private String accessToken;

	private String tokenType;

	public JwtAuthenticationResponse(String token) {
		this.accessToken = token;
	}

}