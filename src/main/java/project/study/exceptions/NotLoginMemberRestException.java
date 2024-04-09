package project.study.exceptions;

import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseDto;

public class NotLoginMemberRestException extends RestFulException {

    public NotLoginMemberRestException() {
        super(new ResponseDto(WebConst.NOT_LOGIN, "로그인이 필요합니다."));
    }
}
