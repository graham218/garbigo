package com.garbigo.service;

import com.garbigo.dto.UserDto;
import com.garbigo.model.User;
import com.garbigo.model.User.AccountStatus;
import com.garbigo.model.User.Role;
import com.garbigo.repository.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public UserService(UserRepository userRepository,
                       CloudinaryService cloudinaryService,
                       PasswordEncoder passwordEncoder,
                       MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Update logged-in user's profile (personal info, address, location, photo)
     */
    public UserDto updateProfile(User user,
                                 String firstName, String middleName, String lastName,
                                 String phoneNumber,
                                 String addressLine1, String addressLine2,
                                 String city, String stateOrProvince,
                                 String postalCode, String country,
                                 Double latitude, Double longitude,
                                 MultipartFile profilePicture) throws IOException {

        // Personal info
        if (firstName != null && !firstName.isBlank()) user.setFirstName(firstName);
        if (middleName != null && !middleName.isBlank()) user.setMiddleName(middleName);
        if (lastName != null && !lastName.isBlank()) user.setLastName(lastName);

        // Phone (with uniqueness check)
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            if (userRepository.existsByPhoneNumber(phoneNumber) && !phoneNumber.equals(user.getPhoneNumber())) {
                throw new RuntimeException("Phone number already in use");
            }
            user.setPhoneNumber(phoneNumber);
        }

        // Address fields
        if (addressLine1 != null) user.setAddressLine1(addressLine1.isBlank() ? null : addressLine1);
        if (addressLine2 != null) user.setAddressLine2(addressLine2.isBlank() ? null : addressLine2);
        if (city != null) user.setCity(city.isBlank() ? null : city);
        if (stateOrProvince != null) user.setStateOrProvince(stateOrProvince.isBlank() ? null : stateOrProvince);
        if (postalCode != null) user.setPostalCode(postalCode.isBlank() ? null : postalCode);
        if (country != null && !country.isBlank()) user.setCountry(country);

        // Geolocation
        if (latitude != null && longitude != null) {
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setLocationUpdatedAt(Instant.now());
        }

        // Profile picture
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String pictureUrl = cloudinaryService.uploadFile(profilePicture);
            user.setProfilePictureUrl(pictureUrl);
        }

        User updatedUser = userRepository.save(user);
        return UserDto.fromUser(updatedUser);
    }

    /**
     * Search users (used by admin) - supports partial match on multiple fields
     */
    public List<UserDto> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userRepository.findAll()
                    .stream()
                    .map(UserDto::fromUser)
                    .toList();
        }

        Pattern pattern = Pattern.compile(query.trim(), Pattern.CASE_INSENSITIVE);

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("email").regex(pattern),
                Criteria.where("firstName").regex(pattern),
                Criteria.where("lastName").regex(pattern),
                Criteria.where("username").regex(pattern),
                Criteria.where("phoneNumber").regex(pattern),
                Criteria.where("city").regex(pattern),
                Criteria.where("addressLine1").regex(pattern)
        );

        Query mongoQuery = new Query(criteria);
        List<User> users = mongoTemplate.find(mongoQuery, User.class);

        return users.stream()
                .map(UserDto::fromUser)
                .toList();
    }

    /**
     * Admin: Create a new user manually
     */
    public UserDto createUser(String email, String password, Role role,
                              String firstName, String middleName, String lastName,
                              String phoneNumber) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (phoneNumber != null && !phoneNumber.isBlank() && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .isEmailVerified(true) // admin-created users are pre-verified
                .createdAt(Instant.now())
                .build();

        User saved = userRepository.save(user);
        return UserDto.fromUser(saved);
    }

    /**
     * Admin: Full update of user (role, status, verification, location)
     */
    public UserDto adminUpdateUser(String userId,
                                   Role role,
                                   AccountStatus status,
                                   Boolean emailVerified,
                                   Boolean phoneVerified,
                                   Double latitude,
                                   Double longitude) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (role != null) user.setRole(role);
        if (status != null) user.setAccountStatus(status);
        if (emailVerified != null) user.setIsEmailVerified(emailVerified);
        if (phoneVerified != null) user.setIsPhoneVerified(phoneVerified);

        if (latitude != null && longitude != null) {
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setLocationUpdatedAt(Instant.now());
        }

        User updated = userRepository.save(user);
        return UserDto.fromUser(updated);
    }

    /**
     * Admin: Delete user
     */
    public void adminDeleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
}