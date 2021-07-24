package com.squadb.workassistantapi.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.squadb.workassistantapi.web.controller.dto.LoginMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckPermissionInterceptor implements HandlerInterceptor {

    private final HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final CheckPermission checkPermission = ((HandlerMethod) handler).getMethodAnnotation(CheckPermission.class);
        if (checkPermission == null) { return true; }

        try {
            final Map<?, ?> pathAttributes = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathAttributes != null && pathAttributes.containsKey("memberId")) {
                final long pathMemberId = Long.parseLong((String)pathAttributes.get("memberId"));
                final LoginMember loginMember = (LoginMember) session.getAttribute("LOGIN_MEMBER");
                return loginMember.isAdmin() || pathMemberId == loginMember.getId();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
