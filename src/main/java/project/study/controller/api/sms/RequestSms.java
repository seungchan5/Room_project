package project.study.controller.api.sms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestSms {

    private String name;
    private String phone;
    private String certification;

}
