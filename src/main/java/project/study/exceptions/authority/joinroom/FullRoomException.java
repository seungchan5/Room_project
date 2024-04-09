package project.study.exceptions.authority.joinroom;

import jakarta.servlet.http.HttpServletResponse;
import project.study.exceptions.authority.AuthorizationException;

public class FullRoomException extends AuthorizationException {
    public FullRoomException(HttpServletResponse response, String alertMessage) {
        super(response, alertMessage);
    }
}
