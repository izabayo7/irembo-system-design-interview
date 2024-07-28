package rw.companyz.useraccountms.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest{
	
	@NotBlank
	private String login;

	@NotBlank
	private String password;

}
