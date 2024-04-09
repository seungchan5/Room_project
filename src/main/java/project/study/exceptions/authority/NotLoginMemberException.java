package project.study.exceptions.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
public class NotLoginMemberException extends AuthorizationException{

    public NotLoginMemberException(HttpServletResponse response) {
        super(response, "로그인이 필요합니다.");
    }
}
