package project.study.exceptions.admin;

import jakarta.servlet.http.HttpServletResponse;
import project.study.dto.abstractentity.ResponseDto;

public class AlreadyExpireRoomException extends AdminException{

    public AlreadyExpireRoomException(HttpServletResponse response, String alertMessage) {
        super(response, alertMessage);
    }
}
