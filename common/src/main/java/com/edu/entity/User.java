package com.edu.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 用户实体 _ 使用datajpa，自动在数据库生成表
 * @Author: WangSong
 * @CreateDate: 2020/7/16 1:46
 */
@Entity
public class User implements UserDetails{

    /** 字符属性 **/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //主键自增长
    private Long userId;
    private String username;
    private String password;

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY) //解决json序列化时字段报null
    private String email;//用户邮箱

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY) //解决json序列化时字段报null
    private String createTime; //创建时间

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY) //解决json序列化时字段报null
    private String lastModifyTime;    //修改时间

    //配置多对多关系(CascadeType.REFRESH级联刷新，一方更新了，另一方获取对方的最新数据)
    @ManyToMany(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER) //fetch = FetchType.EAGER 预加载数据
    //配置用户和权限的关联表
    @JoinTable(name = "users_roles", //中间表名称
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"),//用户id
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "roleId")//用户角色id
    )
    private List<Role> roles;



    /** 属性方法 **/
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    @JsonBackReference//解决序列化时无限递归
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }


    /** 重写父类方法 **/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //加载当前用户所拥有的全部角色
        List<GrantedAuthority> auths = new ArrayList<>();
        List<Role> roles=this.getRoles();
        for(Role role:roles){
            auths.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return auths;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     *  实现userDetils的所有方法必须全部设置成true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", createTime='" + createTime + '\'' +
                ", lastModifyTime='" + lastModifyTime + '\'' +
                ", roles=" + roles +
                '}';
    }
}
