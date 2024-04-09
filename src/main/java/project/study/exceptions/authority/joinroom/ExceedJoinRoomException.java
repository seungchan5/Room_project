package project.study.exceptions.authority.joinroom;

import jakarta.servlet.http.HttpServletResponse;
import project.study.exceptions.authority.AuthorizationException;

public class ExceedJoinRoomException extends AuthorizationException {
    public ExceedJoinRoomException(HttpServletResponse response) {
        super(response, "방 참여 제한을 초과하였습니다.");
    }
}
