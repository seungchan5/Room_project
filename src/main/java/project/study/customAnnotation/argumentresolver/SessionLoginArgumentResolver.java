package project.study.customAnnotation.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import project.study.constant.WebConst;
import project.study.customAnnotation.CallType;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.exceptions.NotLoginMemberRestException;
import project.study.exceptions.authority.NotLoginMemberException;
import project.study.jpaRepository.MemberJpaRepository;

import java.lang.reflect.Parameter;
import java.util.Optional;

import static project.study.constant.WebConst.LOGIN_MEMBER;

@Slf4j
@RequiredArgsConstructor
public class SessionLoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasSessionLoginAnnotation = parameter.hasParameterAnnotation(SessionLogin.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasSessionLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        SessionLogin annotation = parameter.getParameterAnnotation(SessionLogin.class);
        boolean required = (annotation != null) ? annotation.required() : false;
        CallType callType = (annotation != null) ? annotation.type() : CallType.REST_CONTROLLER;

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
        log.info("SessionLogin resolveArgument 실행 uri = {}, required = {}", request.getRequestURI(), annotation.required());

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            hasRequired(required, callType, response);
            return null;
        }

        Long memberId = (Long) session.getAttribute(LOGIN_MEMBER);
        Optional<Member> findMember = memberJpaRepository.findById(memberId);
        if (findMember.isEmpty()) {
            hasRequired(required, callType, response);
            session.removeAttribute(LOGIN_MEMBER);
            return null;
        }

        return findMember.get();
    }

    private void hasRequired(boolean required, CallType callType, HttpServletResponse response) {
        if (required) {
            if (callType == CallType.CONTROLLER) {
                throw new NotLoginMemberException(response);
            }
            if (callType == CallType.REST_CONTROLLER) {
                throw new NotLoginMemberRestException();
            }
        }
    }
}
