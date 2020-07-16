package com.edu.config;

import com.edu.interceptor.LoginSuccessHandle;
import com.edu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * security配置（权限，资源）
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启security注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private DataSource dataSource;//引入jdbc数据源

    //将service方法放入spring中（通过登陆获取到了权限）
    @Bean
    public UserDetailsService loginUserService() { //认证实现组件
        return new UserService();
    }

    //密码安全配置()
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt算法加密，可指定强度
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //注入认证实现组件
        //auth.userDetailsService(customUserService());
        //注入认证实现组件+设置密码加密
        auth.userDetailsService(loginUserService()).passwordEncoder(passwordEncoder());

    }

    //登陆页面请求设置，根据不同角色跳转页面，拦截登陆之前的所有请求
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  //取消放置csrf攻击的防护
                .authorizeRequests() //所有请求设置
                .antMatchers("/css/**", "/js/**", "/img/**", "/fonts/**").permitAll() //允许访问静态文件（不拦截页面样式等）
                .antMatchers("/", "/home", "/registry/**", "/exception/**", "/goto/**").permitAll() //允许访问首页 + 注册页 + 异常请求 + 跳转请求
                .antMatchers("/user/**").permitAll() //允许未登陆用户访问到首页+饮食推荐页
                .anyRequest().authenticated() //其他的请求均需登录验证（认证）
                .and()
                .formLogin().loginPage("/login").failureUrl("/user/login-fail")//指定登陆请求 + 登陆失败请求
                //默认登陆成功后，跳转的请求
                .defaultSuccessUrl("/user/login-success").successHandler(new LoginSuccessHandle()).permitAll() //设置该行为不拦截
                .and()
                .logout().permitAll() //设置注销行为可任意访问
                //设置登录后记住用户，下次自动登录
                //数据库中必须存在名为persistent_logins的表
                /**
                 * 建表sql：
                 * DROP TABLE IF EXISTS `persistent_logins`;
                 * CREATE TABLE `persistent_logins` (
                 *   `username` varchar(64) NOT NULL,
                 *   `series` varchar(64) NOT NULL,
                 *   `token` varchar(64) NOT NULL,
                 *   `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                 *   PRIMARY KEY (`series`)
                 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
                 * SET FOREIGN_KEY_CHECKS=1;
                 */
                .and()
                .rememberMe() //remember me  按钮功能设置
                .tokenValiditySeconds(1209600)
                .tokenRepository(tokenRepository()) //指定记住登录信息所使用的数据源
                .userDetailsService(loginUserService()) //获取用户信息进行校验
                .tokenValiditySeconds(20 * 60);//token有效期60s   24h：24 * 60 * 60

        //解决页面嵌套显示出错
        //解决Refused to display in a frame because it set 'X-Frame-Options' to 'DENY'异常
        http.headers().frameOptions().disable();

        //注销之后跳转的页面
//        http.logout().logoutSuccessUrl("/authentication/require");
    }

    /**
     * 实现login页面的 remember me 功能（spring security 内部都写死了，这里要把 这个DAO 注入）
     *
     * @return
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        /** 存储登陆用户信息到内存（不推荐 : 断电会存在用户信息丢失） **/
        InMemoryTokenRepositoryImpl memory =new InMemoryTokenRepositoryImpl();
        return memory;
        /** 存档到数据库中 **/
//        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
//        db.setDataSource(this.dataSource);
//        return db;
    }

}
