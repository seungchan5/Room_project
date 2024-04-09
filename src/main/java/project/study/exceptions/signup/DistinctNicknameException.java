package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.ERROR;

public class DistinctNicknameException extends SignupException{

    public DistinctNicknameException() {
        super(new ResponseDto(ERROR, "이미 사용 중인 닉네임입니다."));
    }
}