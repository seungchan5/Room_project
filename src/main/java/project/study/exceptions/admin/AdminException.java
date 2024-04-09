package project.study.exceptions.admin;

import jakarta.servlet.http.HttpServletResponse;
import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class AdminException extends RestFulException {

    private final HttpServletResponse response;
    private final String alertMessage;

    public AdminException(HttpServletResponse response, String alertMessage) {
        super(new ResponseDto(WebConst.ERROR, "오류 발생"));
        this.response = response;
        this.alertMessage = alertMessage;
    }

}
