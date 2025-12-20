package com.garbigo.model.request;

import com.garbigo.model.User.Role;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String middleName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "Username must be 3-30 characters and can only contain letters, numbers and underscores")
    private String username;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    private Role role = Role.CLIENT;
}