package project.study.exceptions.admin;

import jakarta.servlet.http.HttpServletResponse;
import project.study.dto.abstractentity.ResponseDto;

public class AlreadyExpireMemberException extends AdminException{

    public AlreadyExpireMemberException(HttpServletResponse response, String alertMessage) {
        super(response, alertMessage);
    }
}
