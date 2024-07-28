package rw.companyz.useraccountms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.companyz.useraccountms.exceptions.BadRequestException;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.domains.ApiResponse;
import rw.companyz.useraccountms.models.dtos.*;
import rw.companyz.useraccountms.security.dtos.*;
import rw.companyz.useraccountms.services.IAuthenticationService;
import rw.companyz.useraccountms.services.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController extends BaseController {
    private final IAuthenticationService authenticationService;
    private final IUserService userService;


    @PostMapping("/signup")
    public ResponseEntity<UserAccount> signup(@Valid  @RequestBody CreateUserDTO request) throws DuplicateRecordException, ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(userService.create(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(
            @RequestBody LoginRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Geolocation", required = false) String geolocation,
            @RequestHeader(value = "Device-Type", required = false) String deviceType
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/signOut")
    public ResponseEntity<ApiResponse<Object>> signOut() throws ResourceNotFoundException {
        authenticationService.signOut();
        return ResponseEntity.ok(new ApiResponse<>(localize("responses.success"), HttpStatus.OK));
    }

    @PostMapping("/signInToken")
    public ResponseEntity<LoginTokenResponseDTO> signInToken(
            @RequestBody LoginRequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.signInToken(request));
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<VerifyTokenResponseDTO> verifyToken(
            @RequestBody LoginTokenResponseDTO request) throws Exception {
        return ResponseEntity.ok(authenticationService.verifyToken(request));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordDTO request) throws ResourceNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>(authenticationService.initiateForgotPassword(request), (Object) "", HttpStatus.OK));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody VerifyOtpDTO request) throws ResourceNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>(authenticationService.verifyOTP(request), "", HttpStatus.OK));
    }


    @GetMapping("/currentUser")
    public ResponseEntity<UserAccount> authUser() throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.getLoggedInUser());
    }

    @Override
    protected String getEntityName() {
        return "Auth";
    }
}
