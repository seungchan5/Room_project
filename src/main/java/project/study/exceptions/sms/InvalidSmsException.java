package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class InvalidSmsException extends SmsException{

    public InvalidSmsException(ResponseDto responseDto) {
        super(responseDto);
    }
}
