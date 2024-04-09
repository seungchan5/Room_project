package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class DistinctAccountException extends SignupException{

    public DistinctAccountException() {
        super(new ResponseDto(ERROR, "이미 사용 중인 아이디 입니다."));
    }
}