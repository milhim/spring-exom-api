package com.milhim.ecommerce.ecommerce.api.controller.auth;

import com.milhim.ecommerce.ecommerce.api.model.LoginBody;
import com.milhim.ecommerce.ecommerce.api.model.LoginResponse;
import com.milhim.ecommerce.ecommerce.api.model.RegistrationBody;
import com.milhim.ecommerce.ecommerce.exception.EmailFailureException;
import com.milhim.ecommerce.ecommerce.exception.UserAlreadyExistException;
import com.milhim.ecommerce.ecommerce.exception.UserNotVerifiedException;
import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            this.userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();

        } catch (UserAlreadyExistException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = this.userService.loginUser(loginBody);
        } catch (UserNotVerifiedException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (e.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);
        response.setSuccess(true);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token) {

        if (userService.verifyUser(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/profile")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {

        return user;
    }
}
