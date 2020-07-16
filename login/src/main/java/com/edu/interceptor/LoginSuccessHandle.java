package com.edu.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 
 * @author WangSong
 * 设置用户登陆成功后跳转的页面，并根据用户角色，跳转页面
 * 根据用户是ROLE_ADMIN还是其他角色使用response.sendRedirect跳转到了不同的页面。
 */
public class LoginSuccessHandle implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException{
		//得到该登陆用户所有权限（角色）
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		//得到请求地址
        String path = request.getContextPath();
        //拼接完整地址（协议模式+ip+端口号+请求地址）
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        //根据用户角色跳转到相应页面（如果该用户的角色包含管理员，发送去管理员页面请求）
        if (roles.contains("ROLE_ADMIN")){
            //response.sendRedirect(basePath+"adminHome");
//            response.sendRedirect(basePath+"home");
            request.setAttribute("role","ROLE_ADMIN");
            return;//方法结束
        }
        //否则，发送登陆普通用户页面请求
//        response.sendRedirect(basePath+"home");
        request.setAttribute("role","ROLE_USER");
	}

}
