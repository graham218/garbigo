package com.garbigo.model.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String profilePictureUrl;
}