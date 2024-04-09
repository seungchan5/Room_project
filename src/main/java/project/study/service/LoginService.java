package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.controller.image.FileUpload;
import project.study.dto.login.DefaultMemberFactory;
import project.study.dto.login.MemberFactory;
import project.study.dto.login.requestdto.RequestDefaultLoginDto;
import project.study.dto.login.requestdto.RequestDefaultSignupDto;
import project.study.jpaRepository.BasicJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.repository.FreezeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final BasicJpaRepository basicJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final FreezeRepository freezeRepository;
    private final BCryptPasswordEncoder encoder;
    private final FileUpload fileUpload;


    @Transactional
    public void login(RequestDefaultLoginDto data, HttpSession session, HttpServletResponse response) {
        MemberFactory factory = new DefaultMemberFactory(basicJpaRepository, memberJpaRepository, freezeRepository, fileUpload, encoder);
        factory.login(data, session, response);
    }

    @Transactional
    public void signup(RequestDefaultSignupDto data) {
        MemberFactory factory = new DefaultMemberFactory(basicJpaRepository, memberJpaRepository, freezeRepository, fileUpload, encoder);
        factory.signup(data);
    }
}
