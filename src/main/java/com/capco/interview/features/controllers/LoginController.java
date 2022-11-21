package com.capco.interview.features.controllers;

import com.capco.interview.features.api.LoginApi;
import com.capco.interview.features.api.model.UserCredentialsBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements LoginApi {

    @Override
    public ResponseEntity<Void> loginUser(UserCredentialsBody userCredentialsBody) {
        // This method always return 200 OK, authentication is done by Spring Security
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
