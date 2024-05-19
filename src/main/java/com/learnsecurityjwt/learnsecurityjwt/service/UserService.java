package com.learnsecurityjwt.learnsecurityjwt.service;

import com.learnsecurityjwt.learnsecurityjwt.models.dto.UserDto;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;

public interface UserService {
    void registerUser(UserDto userDto);
    String userLogin(String email, String password) throws Exception;
}
