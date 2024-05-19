package com.learnsecurityjwt.learnsecurityjwt.controllers;

import com.learnsecurityjwt.learnsecurityjwt.models.dto.UserDto;
import com.learnsecurityjwt.learnsecurityjwt.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMassage=result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMassage);
            }
            if(!userDto.getPassword().equals(userDto.getRePassword())){
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            userService.registerUser(userDto);
            return ResponseEntity.ok("User registered successfully");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDto userDto) {
        try{
            String token=userService.userLogin(userDto.getEmail(),userDto.getPassword());
            return ResponseEntity.ok(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/home")
    public ResponseEntity<String> getHome(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok("hello home");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> testadmin(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok("hello addmin");
    }
}
