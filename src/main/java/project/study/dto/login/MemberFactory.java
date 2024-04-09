package project.study.dto.login;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.domain.Member;
import project.study.domain.Profile;
import project.study.dto.login.requestdto.RequestLoginDto;
import project.study.dto.login.requestdto.RequestSignupDto;
import project.study.dto.login.validator.MemberValidator;
import project.study.jpaRepository.ProfileJpaRepository;

import static project.study.constant.WebConst.LOGIN_MEMBER;

public interface MemberFactory {

    MemberInterface createMember();
    MemberValidator validator();

    FileUpload getFileUpload();


    @Transactional
    default Member signup(RequestSignupDto signupDto) {
        MemberInterface memberInterface = createMember();

        MemberValidator validator = validator();
        validator.validSignup(signupDto);

        Member signup = memberInterface.signup(signupDto);
        saveProfile(signup);

        return signup;
    }

    default void saveProfile(Member member) {
        FileUpload fileUpload = getFileUpload();
        fileUpload.saveFile(null, FileUploadType.MEMBER_PROFILE, member);
    }

    @Transactional
    default Member login(RequestLoginDto loginDto, HttpSession session, HttpServletResponse response) {
        MemberInterface memberInterface = createMember();
        Member member = memberInterface.login(loginDto);

        MemberValidator validator = validator();
        validator.validLogin(member, response);

        if (member != null) {
            session.setAttribute(LOGIN_MEMBER, member.getMemberId());
        }

        return member;
    }

    default String logout(Member member) {
        MemberInterface memberInterface = createMember();
        return memberInterface.logout(member);
    }

}
