package com.thepeacemakers.repository;

import com.thepeacemakers.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByEmailOrUsername(String email, String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByRolesContaining(User.Role role);
    
    List<User> findByAccountStatus(User.AccountStatus status);
    
    @Query("{'$or': [{'firstName': {$regex: ?0, $options: 'i'}}, {'lastName': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}, {'username': {$regex: ?0, $options: 'i'}}]}")
    List<User> searchUsers(String keyword);
    
    @Query("{'$and': [{'isArchived': ?0}, {'isActive': ?1}]}")
    List<User> findByArchiveAndActiveStatus(Boolean isArchived, Boolean isActive);
    
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}