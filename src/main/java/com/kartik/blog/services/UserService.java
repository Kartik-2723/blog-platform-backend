package com.kartik.blog.services;

import com.kartik.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
