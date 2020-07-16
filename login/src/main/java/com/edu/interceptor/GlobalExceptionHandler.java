package com.edu.interceptor;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理拦截类（将该项目请求中后台500错误 + 404，并显示到异常信息显示页面）
 */
@ControllerAdvice       //异常拦截注解
public class GlobalExceptionHandler {

    //设置默认的后端500异常跳转页面
    public static final String DEFAULT_ERROR_VIEW = "error/myError";//注意：这里视图名不能取error，因为springboot默认error已经使用了

    /**
     * 拦截后端500异常
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)  //要拦截的异常范围（Exception拦截所有的后端异常）
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        System.out.println("--------defaultErrorHandler--------");
        System.out.println("拦截的后台异常："+e);
        //创建一个存放异常信息的ModelAndView视图
        ModelAndView modelAndView = new ModelAndView();
        //存放异常信息到model中
        modelAndView.addObject("exception", e);//添加异常信息
        modelAndView.addObject("url", req.getRequestURL());//添加异常请求地址
        modelAndView.setViewName(DEFAULT_ERROR_VIEW);//添加异常跳转视图
        return modelAndView;
    }

    /**
     * 配置拦截项目的404异常
     * @return
     */
    @Bean
    public ConfigurableServletWebServerFactory containerCustomizer() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/exception/to404"));
        return factory;
    }

}
