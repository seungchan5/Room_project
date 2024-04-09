package project.study.controller.api.sms;

import lombok.Getter;
import project.study.dto.abstractentity.ResponseDto;

@Getter
public class ResponseAccountDto extends ResponseDto {

    private final FindAccount findAccount;

    public ResponseAccountDto(String message, FindAccount findAccount) {
        super(message);
        this.findAccount = findAccount;
    }
}
