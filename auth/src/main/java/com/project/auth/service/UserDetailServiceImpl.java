package com.project.auth.service;

import com.project.auth.model.entity.User;
import com.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 5:32 19/05/2025, 2025
 */

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream().map(
                authority -> new SimpleGrantedAuthority(authority.getRoleCode())).collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isActive())
                .accountExpired(user.isAccountExpired())
                .accountLocked(user.isAccountLocked())
                .credentialsExpired(user.isCredentialExpired())
                .authorities(grantedAuthorities)
                .build();
    }
}
