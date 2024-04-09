package project.study.exceptions.login;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class ExpireMemberLoginException extends LoginException{

    public ExpireMemberLoginException() {
        super(new ResponseDto(ERROR, "이미 탈퇴한 회원입니다."));
    }
}
