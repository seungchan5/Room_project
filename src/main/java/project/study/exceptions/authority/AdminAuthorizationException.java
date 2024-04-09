package project.study.exceptions.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

@Getter
public class AdminAuthorizationException  extends RestFulException {

    private final HttpServletResponse response;
    private final String alertMessage;


    public AdminAuthorizationException(HttpServletResponse response, String alertMessage) {
        super(new ResponseDto(WebConst.ERROR, "권한 오류"));
        this.response = response;
        this.alertMessage = alertMessage;
    }
}
