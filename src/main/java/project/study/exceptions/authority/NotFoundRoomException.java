package project.study.exceptions.authority;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundRoomException extends AuthorizationException {
    public NotFoundRoomException(HttpServletResponse response) {
        super(response, "방을 찾을 수 없습니다.");
    }
}
