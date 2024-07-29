package rw.companyz.useraccountms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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


    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserAccount> signup(@Valid  @ModelAttribute CreateUserDTO request, @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        return ResponseEntity.ok(authenticationService.signup(request, file));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(
            @RequestBody LoginRequest request
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/signOut")
    public ResponseEntity<ApiResponse<Object>> signOut() throws ResourceNotFoundException {
        authenticationService.signOut();
        return ResponseEntity.ok(new ApiResponse<>(localize("responses.success"), HttpStatus.OK));
    }

    @PostMapping("/signInToken")
    public ResponseEntity<LoginResponseDTO> signInToken(
            @Valid @RequestBody LoginTokenRequestDTO request) throws Exception {
        return ResponseEntity.ok(authenticationService.signInToken(request));
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<VerifyTokenResponseDTO> verifyToken(
            @Valid @RequestBody LoginTokenRequestDTO request) throws Exception {
        return ResponseEntity.ok(authenticationService.verifyToken(request));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<LoginResponseDTO> forgotPassword(@RequestBody ForgotPasswordDTO request) throws Exception {
        return ResponseEntity.ok(authenticationService.initiateForgotPassword(request));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<LoginResponseDTO> verifyOtp(@RequestBody VerifyOtpDTO request) throws Exception {
        return ResponseEntity.ok(authenticationService.verifyOTP(request));
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
