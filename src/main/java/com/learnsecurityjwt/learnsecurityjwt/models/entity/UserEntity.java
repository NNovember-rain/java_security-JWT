package com.learnsecurityjwt.learnsecurityjwt.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name="user")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "enable")
    private String enable;


    @OneToMany(mappedBy="user",fetch= FetchType.EAGER)
    private List<UserRoleEntity> userRoles=new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // setRole
        List<UserRoleEntity> userRoleEntity=userRoles;
        List<String> roles=new ArrayList<>();
        if(userRoleEntity.size()==0){
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        for(UserRoleEntity userRole:userRoleEntity){
            roles.add(userRole.getRole().getName());
        }
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return authorities;
    }

    @Override
    public String getUsername() { // set Tài khoản
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true; // là true thì sẽ có hiệu lực vô thời hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // không khóa user
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}