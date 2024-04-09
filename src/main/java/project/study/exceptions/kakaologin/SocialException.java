package project.study.exceptions.kakaologin;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

@Getter
public class SocialException extends RestFulException {

    private final HttpServletResponse response;
    private final String alertMessage;


    public SocialException(HttpServletResponse response, String alertMessage) {
        super(new ResponseDto(WebConst.ERROR, "소셜 오류"));
        this.response = response;
        this.alertMessage = alertMessage;
    }
}
