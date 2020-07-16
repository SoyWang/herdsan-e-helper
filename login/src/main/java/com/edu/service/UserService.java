package com.edu.service;

import com.edu.entity.User;
import com.edu.repository.RoleRepository;
import com.edu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * 业务层方法
 *  * 实现spring security提供的UserDetailsService
 *  * 用于加载特定用户信息的
 *  * 它只有一个接口方法用于通过指定的用户名去查询用户
 * @Author: WangSong
 * @CreateDate: 2020/7/16 2:19
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS) //不同的参数值就对应不同的代理类型
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    /**
     * 用户登陆 + 验证（重写UserDetailsService中的该方法）spring security实现解密
     * @param parameter
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String parameter) throws UsernameNotFoundException {
        User user = null;//初始化登陆的用户user
        //判断页面输入的是用户名还是邮箱地址
        if(parameter.contains("@")){
            //用户输入的是邮箱，通过邮箱验证登陆
            List<User> users = userRepository.findByUsernameOrEmail(null,parameter);
            if(!users.isEmpty()){
                user = users.get(0);
            }
        }else {
            //用户输入的是用户名，用过用户名登陆
            List<User> users = userRepository.findByUsernameOrEmail(parameter,null);
            if(users.size() != 0){
                user = users.get(0);
            }
        }
        //判断用户输入是否正确
        if(null == user){
            throw new UsernameNotFoundException("用户不存在！");
        }
        //查找到登陆用户，返回user实体类
        return user;
    }

    /**
     * 判断用户名或者用户邮箱是否已经存在
     * @param name
     * @return
     */
    public boolean checkUserByName(String name) {
        User user = userRepository.findByUsername(name);
        return user != null;
    }
    public boolean checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }


    /**
     * 用户注册
     * 创建一个新的user,角色为ROLE_USER
     * @param userDto
     * @return
     */
    @Transactional  //对数据进行了操作，需要事务注解
    public User registerNewAccount(User userDto) {
        if (checkUserByName(userDto.getUsername()))
            throw new IllegalArgumentException("当前用户名已被使用");
        else if (checkUserByEmail(userDto.getEmail()))
            throw new IllegalArgumentException("当前邮箱已被使用");
        //新建user对象并分配权限，并保存
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        user.setRoles(Arrays.asList(roleRepository.findByRoleName("ROLE_USER")));//为当前用户新建ROLE_USER（使用者）角色
        user.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        user.setLastModifyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //调用保存方法
        User registerUser = userRepository.save(user);
        return registerUser;
    }

    /**
     * 管理员注册新用户
     * @param user
     * @return
     */
    @Transactional  //对数据进行了操作，需要事务注解
    public User registerNewAccountByAdmin(User user) {
        if (checkUserByName(user.getUsername()))
            throw new IllegalArgumentException("当前用户名已被使用");
        else if (checkUserByEmail(user.getEmail()))
            throw new IllegalArgumentException("当前邮箱已被使用");

        user.setRoles(Arrays.asList(roleRepository.findByRoleName("ROLE_USER")));//为当前用户新建ROLE_USER（使用者）角色
        user.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        user.setLastModifyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));//用户密码加密
        //调用保存方法
        User registerUser = userRepository.save(user);
        return registerUser;
    }

    //TODO  修改用户信息

    /**
     * 删除多个用户
     * @param ids
     */
    @Transactional
    public void removeUsers(List<Long> ids){
        this.userRepository.deleteByIds(ids);
    }


}
