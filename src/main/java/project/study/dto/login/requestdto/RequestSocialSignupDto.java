package project.study.dto.login.requestdto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import project.study.domain.SocialToken;
import project.study.enums.SocialEnum;

@Getter
public class RequestSocialSignupDto implements RequestSignupDto{

    private final SocialEnum socialEnum;
    private final SocialToken token;
    private final String name;
    private final String email;
    private String nickName;
    private String phone;
    private final HttpServletResponse response;
    public RequestSocialSignupDto(RequestSocialLoginDto data, HttpServletResponse response) {
        this.socialEnum = data.getSocialEnum();
        this.name = data.getName();
        this.email = data.getEmail();
        this.nickName = data.getNickName();
        this.phone = data.getPhone();
        this.token = data.getToken();
        this.response = response;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
