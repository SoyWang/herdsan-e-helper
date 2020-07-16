package com.edu.repository;

import com.edu.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * role 持久层
 */
public interface RoleRepository extends JpaRepository<Role,Long> {

    /**
     * 通过角色名，找到对应角色信息
     * @param name
     * @return
     */
    Role findByRoleName(String name);
}
