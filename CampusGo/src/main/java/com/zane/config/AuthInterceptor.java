package com.zane.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (isPublicRequest(request)) {
            return true;
        }
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        LoginUser loginUser = (LoginUser) request.getSession().getAttribute(SessionConstants.CURRENT_USER);
        if (loginUser == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, Result.fail(401, "请先登录"));
            return false;
        }

        RequireRole requireRole = getRequireRole(handlerMethod);
        if (requireRole != null && Arrays.stream(requireRole.value()).noneMatch(loginUser.getRole()::equals)) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, Result.fail(403, "无权限访问"));
            return false;
        }

        return true;
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String contextPath = request.getContextPath();
        String path = request.getRequestURI();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return "/api/colleges".equals(path)
                || "/api/carousels".equals(path)
                || "/api/majors".equals(path)
                || "/api/classes".equals(path)
                || "/api/dorm-buildings".equals(path)
                || "/api/dorm-rooms".equals(path)
                || "/api/announcements".equals(path)
                || path.matches("/api/announcements/\\d+");
    }

    private RequireRole getRequireRole(HandlerMethod handlerMethod) {
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (methodRole != null) {
            return methodRole;
        }
        return handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }

    private void writeJson(HttpServletResponse response, int status, Result<Void> result) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
