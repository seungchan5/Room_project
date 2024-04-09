package project.study.exceptions.authority;

import jakarta.servlet.http.HttpServletResponse;

public class NotAuthorizedException extends AuthorizationException {

    public NotAuthorizedException(HttpServletResponse response) {
        super(response, "권한이 없습니다.");
    }
}
