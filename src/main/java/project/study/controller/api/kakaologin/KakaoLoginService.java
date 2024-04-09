package project.study.controller.api.kakaologin;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.controller.image.FileUpload;
import project.study.domain.Member;
import project.study.dto.login.KakaoMemberFactory;
import project.study.dto.login.MemberFactory;
import project.study.dto.login.requestdto.RequestSocialLoginDto;
import project.study.dto.login.requestdto.RequestSocialSignupDto;
import project.study.jpaRepository.SocialTokenJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.SocialJpaRepository;
import project.study.repository.FreezeRepository;

import java.io.IOException;
import java.io.PrintWriter;

import static project.study.constant.WebConst.LOGIN_MEMBER;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {

    private final FreezeRepository freezeRepository;
    private final KakaoLoginRepository kakaoLoginRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final SocialJpaRepository socialJpaRepository;
    private final SocialTokenJpaRepository socialTokenJpaRepository;
    private final FileUpload fileUpload;

    public void login(String code, HttpSession session, HttpServletResponse response) {
        RequestSocialLoginDto data = new RequestSocialLoginDto(code);

        MemberFactory factory = new KakaoMemberFactory(freezeRepository, kakaoLoginRepository, memberJpaRepository, socialJpaRepository, socialTokenJpaRepository, fileUpload);
        Member loginMember = factory.login(data, session, response);
        if (loginMember == null) {
            RequestSocialSignupDto signupDto = new RequestSocialSignupDto(data, response);
            log.info("카카오 회원가입 진행");
            loginMember = factory.signup(signupDto);
            session.setAttribute(LOGIN_MEMBER, loginMember.getMemberId());
        }
        execute(response);
    }

    private String execute(HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> opener.location.href='/'; window.self.close(); </script>";
        try (PrintWriter out = response.getWriter()) {
            out.println(command);
            out.flush();
        } catch (IOException e) {
            log.error("Alert IOException 발생!");
        }
        return null;
    }

    public String logout(Member member) {
        MemberFactory factory = new KakaoMemberFactory(freezeRepository, kakaoLoginRepository, memberJpaRepository, socialJpaRepository, socialTokenJpaRepository, fileUpload);
        return factory.logout(member);
    }
}
