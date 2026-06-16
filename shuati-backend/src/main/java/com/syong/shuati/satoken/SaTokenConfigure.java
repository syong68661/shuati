package com.syong.shuati.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
                    String method = SaHolder.getRequest().getMethod();
                    if ("OPTIONS".equalsIgnoreCase(method)) {
                        return;
                    }
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/error",
                        "/doc.html",
                        "/favicon.ico",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/home/statistics",
                        "/question/get/vo",
                        "/questionBank/list/page/vo",
                        "/questionBank/get/vo",
                        "/question/list/page/vo",
                        "/user/register",
                        "/user/login",
                        "/user/login/wx_open",
                        "/test/user/doLogin",
                        "/test/user/isLogin"
                );
    }
}

