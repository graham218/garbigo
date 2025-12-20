package com.thepeacemakers.security;

import com.thepeacemakers.model.User;
import com.thepeacemakers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrUsername.toLowerCase())
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername.toLowerCase())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + emailOrUsername)));
        
        // Check if user is active
        if (user.getAccountStatus() != User.AccountStatus.ACTIVE && 
            user.getAccountStatus() != User.AccountStatus.PENDING) {
            throw new UsernameNotFoundException("User account is not active");
        }
        
        // Build authorities from role and permissions
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsEmailVerified() != null && user.getIsEmailVerified(),
                true, // account non-expired
                true, // credentials non-expired
                user.getAccountStatus() != User.AccountStatus.BLOCKED && 
                user.getAccountStatus() != User.AccountStatus.SUSPENDED,
                authorities
        );
    }
}