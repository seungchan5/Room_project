package project.study.controller.api.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChangePassword extends RequestFindPassword {

    private String password;
    private String passwordCheck;


}
