package project.study.exceptions.sms;

import project.study.dto.abstractentity.ResponseDto;

public class NotSupportedSocialMemberException extends SmsException{

    public NotSupportedSocialMemberException(ResponseDto responseDto) {
        super(responseDto);
    }
}
