package com.learnsecurityjwt.learnsecurityjwt.repository;

import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;
import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
}
