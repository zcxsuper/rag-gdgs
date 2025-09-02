package com.example.interceptor;

import com.example.exception.TokenInvalidException;
import com.example.util.JwtUtil;
import com.example.util.TokenRedisUtil;
import com.example.util.UserContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 线程级别管理用户信息
 */
public class UserInterceptor implements HandlerInterceptor {

    private TokenRedisUtil tokenRedisUtil;
    private JwtUtil jwtUtil;

    /**
     * 请求到达 Controller 方法之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IllegalAccessException {
        // 获取由网关记录的userId的值
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            throw new TokenInvalidException("缺少token");
        }
        Long userId = jwtUtil.verifyToken(token, response);
        UserContextUtil.setUserId(userId);
        return true;
    }

    /**
     * 整个请求完成之后（包括视图渲染后或 Controller 抛异常后）
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextUtil.clear();
    }
}
