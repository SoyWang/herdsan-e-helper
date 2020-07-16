package com.edu.controller;

import com.edu.entity.Role;
import com.edu.entity.User;
import com.edu.repository.UserRepository;
import com.edu.service.UserService;
import com.edu.vo.ResponseMsg;
import com.edu.vo.RetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Description: 用户操作接口
 * @Author: WangSong
 * @CreateDate: 2020/7/16 3:00
 */
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    /**
     * 用户注册
     */
    @PostMapping("add")
    public Callable<RetResult<String>> add(@RequestBody User userDto){
        Callable<RetResult<String>> callable = new Callable<RetResult<String>>() {
            @Override
            public RetResult<String> call() throws Exception {
                //判断 用户名&邮箱 是否已经存在
                Boolean nameResult = userService.checkUserByName(userDto.getUsername());
                Boolean emailResult = userService.checkUserByEmail(userDto.getEmail());
                if(nameResult){
                    //已经存在该用户，方法结束
                    return ResponseMsg.makeRsp(400,"当前用户已被注册");
                }
                if(emailResult){
                    return ResponseMsg.makeRsp(400,"当前邮箱已被注册");
                }
                //调用注册方法
                User user = userService.registerNewAccount(userDto);
                return ObjectUtils.isEmpty(user) ? ResponseMsg.makeErrRsp("注册失败"):ResponseMsg.makeOKRsp();
            }
        };
        return callable;
    }

    /**
     * 用户登录（已经在配置类实现） （//默认登录认证地址：/login）
     */
    @RequestMapping("login-success")
    public RetResult loginSuccess(){
        return ResponseMsg.makeOKRsp();
    }
    @RequestMapping("login-fail")
    public RetResult loginFail(){
        return ResponseMsg.makeErrRsp("用户名邮箱密码不匹配");
    }


    /**
     * 用户退出（已经在配置类实现） //默认注销地址：/logout
     */


    /**
     * 加载用户信息
     * @throws Exception
     */
    @GetMapping("info")
    public RetResult<List> info() {
        //获取当前登陆的用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //通过登陆用户名 & 邮箱查询到用户
        User user = (User) userService.loadUserByUsername(username);
        //异常处理
        if(null == user){
            return ResponseMsg.makeRsp(417,"用户会话超时，需要重新登录");
        }
        //判断当前登陆用户是否拥有管理员权限
        List<Role> roles = user.getRoles();
        boolean isAdmin = false;//定义一个用户是否拥有管理员权限的标志（默认没有）
        //如果已经找到管理员权限，则不再循环。
        loop : for(Role role:roles){
            if("ROLE_ADMIN".equals(role.getRoleName())){
                isAdmin = true; //定义当前用户是管理员
                break loop;
            }
        }
        List<User> users = new ArrayList<>();//定义一个装用户的集合
        //如果是管理员，加载全部用户
        if(isAdmin){
            users = userRepository.findAll();
        }
        //如果是普通用户，只加载当前用户的信息
        if(!isAdmin){
            users.add(user);//存入当前用户
        }
        return ResponseMsg.makeOKRsp(users);
    }

    //TODO    用户信息修改接口
}
