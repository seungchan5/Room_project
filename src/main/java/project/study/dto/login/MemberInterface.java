package project.study.dto.login;

import project.study.domain.Member;
import project.study.dto.login.requestdto.RequestLoginDto;
import project.study.dto.login.requestdto.RequestSignupDto;

public interface MemberInterface {


    Member signup(RequestSignupDto signupDto);


    Member login(RequestLoginDto loginDto);


    String logout(Member member);
}
