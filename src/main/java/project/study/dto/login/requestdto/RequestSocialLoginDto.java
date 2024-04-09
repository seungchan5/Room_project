package project.study.dto.login.requestdto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import project.study.domain.SocialToken;
import project.study.enums.SocialEnum;


@Getter
@Setter
@ToString
public class RequestSocialLoginDto implements RequestLoginDto {

    private final String code;
    private SocialToken token;
    private SocialEnum socialEnum;
    private String name;
    private String email;
    private String nickName;
    private String phone;

    public RequestSocialLoginDto(String code) {
        this.code = code;
    }

}
