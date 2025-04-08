package com.oneultanda.userservice.presentaion.controller;

import com.oneultanda.userservice.application.dto.comand.CreateUserCommand;
import com.oneultanda.userservice.application.service.UserService;
import com.oneultanda.userservice.presentaion.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userservice;
    @PostMapping("/signup")
    public ResponseEntity<Void> CreateUser(CreateUserRequest request) {
        userservice.createUser(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @GetMapping
//
//
//    @PutMapping
//
//    @DeleteMapping
}
