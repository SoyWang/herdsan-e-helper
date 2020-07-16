package com.edu.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * @Description: 角色实体类
 * @Author: WangSong
 * @CreateDate: 2020/7/16 1:57
 */
@Entity
public class Role {

    @Id
    @GeneratedValue
    private Integer roleId;
    private String roleName;

    //配置用户权限关系
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;


    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }


    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
