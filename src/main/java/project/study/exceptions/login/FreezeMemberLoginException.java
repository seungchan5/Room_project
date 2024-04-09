package project.study.exceptions.login;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class FreezeMemberLoginException extends LoginException {

    public FreezeMemberLoginException(String message) {
        super(new ResponseDto(ERROR, message));
    }
}
