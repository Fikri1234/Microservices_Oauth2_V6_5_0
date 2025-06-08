package com.project.auth.service.impl;

import com.project.auth.model.entity.User;

import java.util.Optional;

/**
 * Created by user on 5:28 19/05/2025, 2025
 */
public interface UserService {

    User save(User entity);
    User update(User entity);
    void deleteById(Long id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken (String resetToken);
    Optional<User> findById(Integer id);
}
