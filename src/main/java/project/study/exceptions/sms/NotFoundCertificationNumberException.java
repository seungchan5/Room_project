package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class NotFoundCertificationNumberException extends SmsException {

    public NotFoundCertificationNumberException(ResponseDto responseDto) {
        super(responseDto);
    }
}
