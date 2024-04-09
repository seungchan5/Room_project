package project.study.exceptions.authority;

import jakarta.servlet.http.HttpServletResponse;

public class NotJoinRoomException extends AuthorizationException{
    public NotJoinRoomException(HttpServletResponse response) {
        super(response, "참여한 방이 아닙니다.");
    }
}
