package com.squadb.workassistantapi.web.interceptor;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckPermissionInterceptor implements HandlerInterceptor {

    private final HttpSession session;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final CheckPermission checkPermission = ((HandlerMethod) handler).getMethodAnnotation(CheckPermission.class);
        if (checkPermission == null) {
            return true;
        }

        try {
            final long loginMemberId = Long.parseLong(String.valueOf(session.getAttribute(Member.LOGIN_SESSION_KEY)));
            final Map pathAttributes = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathAttributes != null && pathAttributes.containsKey("memberId")) {
                final long memberId = Long.parseLong((String)pathAttributes.get("memberId"));
                if (memberId == loginMemberId) return true;
                final Member loginMember = memberService.findById(loginMemberId);
                if (loginMember.isAdmin()) return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
