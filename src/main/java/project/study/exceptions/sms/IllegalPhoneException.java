package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class IllegalPhoneException extends SmsException{

    public IllegalPhoneException() {
        super(new ResponseDto("error", "휴대폰 번호를 다시 확인해주세요."));
    }
}
