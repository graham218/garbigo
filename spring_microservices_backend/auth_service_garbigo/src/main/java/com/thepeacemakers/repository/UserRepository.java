package com.thepeacemakers.repository;

import com.thepeacemakers.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    Optional<User> findByGoogleId(String googleId);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByAccountStatus(User.AccountStatus status);
    
    List<User> findByIsEmailVerified(Boolean isEmailVerified);
    
    Page<User> findByArchived(Boolean archived, Pageable pageable);
    
    @Query("{$or: ["
            + "{'firstName': {$regex: ?0, $options: 'i'}}, "
            + "{'lastName': {$regex: ?0, $options: 'i'}}, "
            + "{'email': {$regex: ?0, $options: 'i'}}, "
            + "{'username': {$regex: ?0, $options: 'i'}}, "
            + "{'phoneNumber': {$regex: ?0, $options: 'i'}}"
            + "]}")
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    
    @Query("{$and: ["
            + "{$or: ["
            + "{'firstName': {$regex: ?0, $options: 'i'}}, "
            + "{'lastName': {$regex: ?0, $options: 'i'}}, "
            + "{'email': {$regex: ?0, $options: 'i'}}, "
            + "{'username': {$regex: ?0, $options: 'i'}}, "
            + "{'phoneNumber': {$regex: ?0, $options: 'i'}}"
            + "]}, "
            + "{'archived': ?1}"
            + "]}")
    Page<User> searchUsersByArchiveStatus(String searchTerm, Boolean archived, Pageable pageable);
}