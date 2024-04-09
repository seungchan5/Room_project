package project.study.exceptions.authority.joinroom;

import jakarta.servlet.http.HttpServletResponse;
import project.study.exceptions.authority.AuthorizationException;

public class InvalidPublicPasswordException extends AuthorizationException {
    public InvalidPublicPasswordException(HttpServletResponse response, String alertMessage) {
        super(response, alertMessage);
    }
}
