package com.thepeacemakers.model.request;

import com.thepeacemakers.model.User.AccountStatus;
import com.thepeacemakers.model.User.Role;
import lombok.Data;
import java.util.Set;

@Data
public class AdminUpdateUserRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private Set<Role> roles;
    private AccountStatus accountStatus;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean isActive;
    private Boolean isArchived;
}