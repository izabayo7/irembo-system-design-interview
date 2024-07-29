package rw.companyz.useraccountms.services;


import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.dtos.CreateUserDTO;
import rw.companyz.useraccountms.models.dtos.ForgotPasswordDTO;
import rw.companyz.useraccountms.models.dtos.SignupDTO;
import rw.companyz.useraccountms.models.dtos.VerifyOtpDTO;
import rw.companyz.useraccountms.security.dtos.*;

import java.util.UUID;

public interface IAuthenticationService {


    LoginResponseDTO signin(LoginRequest request) throws ResourceNotFoundException;
    UserAccount signup(CreateUserDTO request, MultipartFile file) throws Exception;

    void signOut() throws ResourceNotFoundException;

    void invalidateUserLogin(UserAccount userAccount);

    LoginResponseDTO signInToken(LoginTokenRequestDTO request) throws Exception;

    VerifyTokenResponseDTO verifyToken(LoginTokenRequestDTO request) throws Exception;

    UserAccount getAuthenticatedUser(String token) throws ResourceNotFoundException;

    LoginResponseDTO initiateForgotPassword(ForgotPasswordDTO forgotPassword) throws Exception;

    LoginResponseDTO verifyOTP(VerifyOtpDTO verifyOtp) throws Exception;

    UserAccount verifyUserOwnership(UUID userId) throws ResourceNotFoundException;
}
