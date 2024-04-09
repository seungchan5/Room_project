package project.study.exceptions.login;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class InvalidLoginException extends LoginException {

    public InvalidLoginException() {
        super(new ResponseDto(ERROR, "아이디 또는 비밀번호가 잘못되었습니다."));
    }

}

