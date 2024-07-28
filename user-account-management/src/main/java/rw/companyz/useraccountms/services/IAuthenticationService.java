package rw.companyz.useraccountms.services;


import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.dtos.ForgotPasswordDTO;
import rw.companyz.useraccountms.models.dtos.VerifyOtpDTO;
import rw.companyz.useraccountms.security.dtos.*;

import java.util.UUID;

public interface IAuthenticationService {


    LoginResponseDTO signin(LoginRequest request) throws ResourceNotFoundException;

    void signOut() throws ResourceNotFoundException;

    void invalidateUserLogin(UserAccount userAccount);

    LoginTokenResponseDTO signInToken(LoginRequest request) throws Exception;

    VerifyTokenResponseDTO verifyToken(LoginTokenResponseDTO request) throws Exception;

    UserAccount getAuthenticatedUser(String token) throws ResourceNotFoundException;

    String initiateForgotPassword(ForgotPasswordDTO forgotPassword) throws  ResourceNotFoundException;

    boolean verifyOTP(VerifyOtpDTO verifyOtp) throws ResourceNotFoundException;

    UserAccount verifyUserOwnership(UUID userId) throws ResourceNotFoundException;
}
