package project.study.exceptions.kakaologin;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AlreadySignupMemberException extends SocialException {
    public AlreadySignupMemberException(HttpServletResponse response) {
        super(response, "가입 이력이 존재합니다.");
    }

}
