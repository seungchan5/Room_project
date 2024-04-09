package project.study.controller.api.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFindPassword extends RequestSms {

    private String account;
}
