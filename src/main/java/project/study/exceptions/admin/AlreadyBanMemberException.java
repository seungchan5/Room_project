package project.study.exceptions.admin;

import jakarta.servlet.http.HttpServletResponse;

public class AlreadyBanMemberException extends AdminException{

    public AlreadyBanMemberException(HttpServletResponse response, String alertMessage) {
        super(response, alertMessage);
    }
}
