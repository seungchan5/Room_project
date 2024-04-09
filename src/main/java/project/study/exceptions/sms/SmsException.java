package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class SmsException extends RestFulException {

    public SmsException(ResponseDto responseDto) {
        super(responseDto);
    }
}
