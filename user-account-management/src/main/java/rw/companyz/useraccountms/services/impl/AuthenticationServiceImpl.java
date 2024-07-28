package rw.companyz.useraccountms.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.exceptions.*;
import rw.companyz.useraccountms.models.*;
import rw.companyz.useraccountms.models.dtos.ForgotPasswordDTO;
import rw.companyz.useraccountms.models.dtos.VerifyOtpDTO;
import rw.companyz.useraccountms.models.enums.ELoginStatus;
import rw.companyz.useraccountms.models.enums.EOTPStatus;
import rw.companyz.useraccountms.models.enums.EUserStatus;
import rw.companyz.useraccountms.repositories.IUserRepository;
import rw.companyz.useraccountms.security.dtos.*;
import rw.companyz.useraccountms.services.*;
import rw.companyz.useraccountms.utils.Constants;
import rw.companyz.useraccountms.utils.EncryptionService;
import rw.companyz.useraccountms.utils.OTPUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final IUserRepository userRepository;

    private final IJwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EncryptionService encryptionService;

    private final IUserRoleService userRoleReService;

    private  final IRolePrivilegeService rolePrivilegeService;

    private final IUserService userService;
//    private final INotificationService notificationService;

    @Override
    public LoginResponseDTO signin(LoginRequest request) throws ResourceNotFoundException {

            UserAccount user = null;
            request.setLogin(request.getLogin().trim());
            request.setPassword(request.getPassword().trim());

         try{
            if(request.getLogin().matches("\\d+")){
                user = userRepository.findByEmailAddress(request.getLogin()).orElseThrow(()->new InvalidCredentialsException("exceptions.badRequest.invalidOtp"));
            }else{
                user = userRepository.findByEmailAddress(request.getLogin()).orElseThrow(InvalidCredentialsException::new);
            }

             // fail when (1) have login have failed multiple times, (2) when user is not found
             if(user.getStatus().equals(EUserStatus.ADMIN_LOCKED) || user.getStatus().equals(EUserStatus.DELETED) || user.getStatus().equals(EUserStatus.INACTIVE))
                 throw new InvalidCredentialsException("exceptions.accountLocked");

             // auth user
             authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));


             //if valid credentials, save last login
             user.setLastLogin(LocalDateTime.now(ZoneId.of("Africa/Kigali")));
             user.setAccountLocked(false);
             userRepository.save(user);

             var jwt = generateJWTToken(user);

             return LoginResponseDTO.builder().token(jwt).build();

         }catch(Exception e){
            log.info("Exception: " + e.getMessage());
             if(user != null){
                 if(user.getStatus().equals(EUserStatus.ADMIN_LOCKED)){
                     throw new InvalidCredentialsException("exceptions.accountLocked");
                 }
             }
             if(request.getLogin().matches("\\d+")){
                 throw new InvalidCredentialsException("exceptions.badRequest.invalidOtp");

             }else{
                 throw new InvalidCredentialsException("exceptions.invalidEmailPassword");
             }
         }
    }

    @Override
    public void signOut() throws ResourceNotFoundException {
       UserAccount userAccount = userService.getLoggedInUser();

       invalidateUserLogin(userAccount);
    }

    @Override
    public void invalidateUserLogin(UserAccount userAccount){
        userAccount.setSessionId(null);
        userAccount.setLoginStatus(ELoginStatus.INACTIVE);

        userRepository.save(userAccount);
    }

    @Override
    public LoginTokenResponseDTO signInToken(LoginRequest request) throws Exception {
        this.signin(request);

        UserAccount user = userRepository.findByEmailAddress(request.getLogin()).orElseThrow(InvalidCredentialsException::new);

        String token = UUID.randomUUID().toString();
        user.setAuthToken(encryptionService.encrypt(token));
        user.setAuthTokenExpiryDate(LocalDateTime.now().plusYears(1));
        userRepository.save(user);

        return LoginTokenResponseDTO.builder().token(token).build();

    }


    @Override
    public VerifyTokenResponseDTO verifyToken(LoginTokenResponseDTO request) throws Exception {

        String encryptedToken = encryptionService.encrypt(request.getToken());
        UserAccount user = userRepository.findByAuthToken(encryptedToken).orElseThrow(InvalidCredentialsException::new);

        var jwtToken = generateJWTToken(user);

        return VerifyTokenResponseDTO.builder().userAccount(user).token(jwtToken).build();

    }

    @Override
    public UserAccount getAuthenticatedUser(String token) throws ResourceNotFoundException {
        String  username=  this.jwtService.extractUserName(token);
        Optional<UserAccount> userAccount = this.userRepository.findByEmailAddress(username);
        if (userAccount.isPresent()) {
            return userAccount.get();
        }
        else throw new ResourceNotFoundException("User", "username", username);
    }
    @Override
    public String initiateForgotPassword(ForgotPasswordDTO forgotPassword) throws  ResourceNotFoundException {
        Optional<UserAccount> userAccount = this.userRepository.findByEmailAddress(forgotPassword.getEmailAddress());
        if(userAccount.isPresent()){
            UserAccount user = userAccount.get();
            LocalDateTime now = LocalDateTime.now();

            String otp = OTPUtil.generateOtp();

            LocalDateTime oneHourLater = now.plusMinutes(5);
            user.setOtp(otp);
            user.setOtpStatus(EOTPStatus.NOT_USED);
            user.setOtpExpiryDate(oneHourLater);

//            notificationService.sendNotification(NotificationPayload.builder()
//                    .notificationType(NotificationType.EMAIL)
//                    .destinations(List.of(userAccount.get().getEmailAddress()))
//                    .subject("Reset Password Initiated")
//                    .templatePayload(TemplatePayload.builder().templateName(EmailTemplate.RESETPASSWORD).data(Map.of("verificationCode", otp)).build())
//                    .build());


            this.userRepository.save(user);
            return otp;

        } else throw new ResourceNotFoundException("User", "email", forgotPassword.getEmailAddress());
    }

    @Override
    public boolean verifyOTP(VerifyOtpDTO verifyOtp) throws ResourceNotFoundException {
        Optional<UserAccount> userAccount = this.userRepository.findByEmailAddress(verifyOtp.getEmailAddress());
        if (userAccount.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", verifyOtp.getEmailAddress());
        }

        UserAccount user = userAccount.get();
        LocalDateTime now = LocalDateTime.now();

        boolean isOtpExpired = user.getOtpExpiryDate() != null && (now.isAfter(user.getOtpExpiryDate()));

        if (isOtpExpired) {
            user.setOtpStatus(EOTPStatus.EXPIRED);
            this.userRepository.save(user);
            throw new BadRequestAlertException("exceptions.badRequest.expiredOtp");
        }

        boolean isOtpNotUsed = user.getOtpStatus() != null && user.getOtpStatus() == EOTPStatus.NOT_USED;
        boolean isOtpMatching = user.getOtp() != null ? user.getOtp().equals(verifyOtp.getOtp()) : false;
        if (!isOtpMatching && isOtpNotUsed) {
            throw new BadRequestAlertException("exceptions.badRequest.invalidOtp");
        }

        user.setOtpStatus(EOTPStatus.VERIFIED);
        this.userRepository.save(user);

        return true;
    }

    @Override
    public UserAccount verifyUserOwnership(UUID userId) throws ResourceNotFoundException {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        UserAccount findByEmail = userRepository.findByEmailAddress(username).orElseThrow(() -> new ResourceNotFoundException("UserAccount", "email", username));

        if( findByEmail.getId() != userId) throw new BadRequestAlertException("You are not allowed to perform this action");

        return findByEmail;
    }


    private JwtAuthenticationResponse generateJWTToken(UserAccount user) throws ResourceNotFoundException {
        List<UserAccountRole> userRoles = userRoleReService.getAllActiveByUserId(user);
        List<GrantedAuthority> privileges = new ArrayList<>();

        for(UserAccountRole userAccountRole:userRoles){
            privileges.addAll(this.rolePrivilegeService.getAllByRole(userAccountRole.getRole()));
        }

        user.setRoles(userRoles);
        user.setAuthorities(privileges);

        return JwtAuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(UserDetailsImpl.build(user))).tokenType(Constants.TOKEN_TYPE).build();
    }
}
