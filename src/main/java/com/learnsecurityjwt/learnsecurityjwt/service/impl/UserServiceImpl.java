package com.learnsecurityjwt.learnsecurityjwt.service.impl;

import com.learnsecurityjwt.learnsecurityjwt.converter.UserConverter;
import com.learnsecurityjwt.learnsecurityjwt.jwt.JwTokenUtil;
import com.learnsecurityjwt.learnsecurityjwt.models.dto.UserDto;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.RoleEntity;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserRoleEntity;
import com.learnsecurityjwt.learnsecurityjwt.repository.UserRepository;
import com.learnsecurityjwt.learnsecurityjwt.repository.UserRoleRepository;
import com.learnsecurityjwt.learnsecurityjwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private JwTokenUtil jwTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserDto userDto) {
        Optional<UserEntity>checkEmail = userRepository.findByEmail(userDto.getEmail());
        if (checkEmail.isPresent()) {
            throw new DataIntegrityViolationException("Email address already in use");
        }
        RoleEntity role = new RoleEntity();
        role.setId(3L);
        UserEntity userEntity=userConverter.toUserEntity(userDto);
        UserRoleEntity userRoleEntity=new UserRoleEntity();
        userRoleEntity.setRole(role);
        userRoleEntity.setUser(userEntity);
        userRepository.save(userEntity);
        userRoleRepository.save(userRoleEntity);

    }

    @Override
    public String userLogin(String email, String password) throws Exception {
        Optional<UserEntity> optionalUser=userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Invalid email / password");
        }
        UserEntity existingUser= optionalUser.get();

        //check password
        if(!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong email or password");
        }

        // authenticate với spring security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password,existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        // trả về token
        return jwTokenUtil.generateToken(optionalUser.get());
    }
}
