package project.study.dto.login.requestdto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class RequestDefaultLoginDto implements RequestLoginDto {

    private String account;
    private String password;
}
