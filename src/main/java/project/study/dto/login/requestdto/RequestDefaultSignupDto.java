package project.study.dto.login.requestdto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestDefaultSignupDto implements RequestSignupDto{

    private String account;
    private String password;
    private String passwordCheck;
    private String name;
    private String nickName;
}
