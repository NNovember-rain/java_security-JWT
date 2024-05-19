package com.learnsecurityjwt.learnsecurityjwt.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user_role")
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_user")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="id_role")
    private RoleEntity role;

}

