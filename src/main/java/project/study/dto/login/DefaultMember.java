package project.study.dto.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import project.study.common.CustomDateTime;
import project.study.domain.Basic;
import project.study.domain.Member;
import project.study.dto.login.requestdto.RequestDefaultLoginDto;
import project.study.dto.login.requestdto.RequestDefaultSignupDto;
import project.study.dto.login.requestdto.RequestLoginDto;
import project.study.dto.login.requestdto.RequestSignupDto;
import project.study.enums.MemberStatusEnum;
import project.study.exceptions.login.InvalidLoginException;
import project.study.jpaRepository.BasicJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class DefaultMember implements MemberInterface {

    private final BasicJpaRepository basicJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final BCryptPasswordEncoder encoder;


    @Transactional
    @Override
    public Member signup(RequestSignupDto signupDto) {
        RequestDefaultSignupDto data = (RequestDefaultSignupDto) signupDto;

        Member saveMember = Member.builder()
                .memberName(data.getName())
                .memberNickname(data.getNickName())
                .memberCreateDate(CustomDateTime.now())
                .memberStatus(MemberStatusEnum.정상)
                .build();
        memberJpaRepository.save(saveMember);

        String salt = UUID.randomUUID().toString().substring(0, 6);

        Basic saveBasic = Basic.builder()
                .account(data.getAccount())
                .password(encoder.encode(data.getPassword() + salt))
                .salt(salt)
                .member(saveMember)
                .build();
        basicJpaRepository.save(saveBasic);

        return saveMember;
    }

    @Override
    public Member login(RequestLoginDto loginDto) {
        RequestDefaultLoginDto data = (RequestDefaultLoginDto) loginDto;

        Basic basic = checkAccount(data);
        checkPassword(basic, data);

        return basic.getMember();
    }

    @Override
    public String logout(Member member) {

        return "/";
    }

    private Basic checkAccount(RequestDefaultLoginDto data) {
        Optional<Basic> findAccount = basicJpaRepository.findByAccount(data.getAccount());
        if (findAccount.isEmpty()) throw new InvalidLoginException(); // 아이디가 존재하지않음
        return findAccount.get();
    }

    private void checkPassword(Basic basic, RequestDefaultLoginDto data) {
        boolean validPassword = basic.isValidPassword(encoder, data.getPassword());
        if (!validPassword) throw new InvalidLoginException(); // 비밀번호가 일치하지 않음
    }
}
