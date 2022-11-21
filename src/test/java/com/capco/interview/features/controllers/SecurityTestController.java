package com.capco.interview.features.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("/security-test")
    public ResponseEntity<String> dummyMethod() {
        return ResponseEntity.ok("Hello Secured World");
    }
}
