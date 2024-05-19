package com.learnsecurityjwt.learnsecurityjwt.converter;

import com.learnsecurityjwt.learnsecurityjwt.models.dto.UserDto;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userEntity;
    }
}
