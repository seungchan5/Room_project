package project.study.dto.login;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.study.common.CustomDateTime;
import project.study.controller.api.kakaologin.KakaoLoginRepository;
import project.study.domain.SocialToken;
import project.study.domain.Member;
import project.study.domain.Social;
import project.study.dto.login.requestdto.*;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;
import project.study.jpaRepository.SocialTokenJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.SocialJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class KakaoMember implements MemberInterface{

    private final KakaoLoginRepository kakaoLoginRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final SocialJpaRepository socialJpaRepository;
    private final SocialTokenJpaRepository socialTokenJpaRepository;

    @Transactional
    @Override
    public Member signup(RequestSignupDto signupDto) {
        RequestSocialSignupDto data = (RequestSocialSignupDto) signupDto;

        Member saveMember = Member.builder()
                .memberName(data.getName())
                .memberNickname(data.getNickName())
                .memberCreateDate(CustomDateTime.now())
                .memberStatus(MemberStatusEnum.정상)
                .phone(data.getPhone())
                .build();
        memberJpaRepository.save(saveMember);

        Social saveSocial = Social.builder()
                .socialType(SocialEnum.KAKAO)
                .socialEmail(data.getEmail())
                .member(saveMember)
                .build();
        socialJpaRepository.save(saveSocial);


        SocialToken token = data.getToken();
        token.setSocial(saveSocial);
        socialTokenJpaRepository.save(token);

        return saveMember;
    }

    @Override
    public Member login(RequestLoginDto loginDto) {
        RequestSocialLoginDto data = (RequestSocialLoginDto) loginDto;

        SocialToken socialToken = kakaoLoginRepository.getKakaoAccessToken(data.getCode());
        kakaoLoginRepository.getUserInfo(data, socialToken);

        System.out.println("loginDto = " + loginDto);

        Optional<Social> findMember = kakaoLoginRepository.findBySocialAndEmail(data.getSocialEnum(), data.getEmail());
        if (findMember.isEmpty()) return null;
        Social social = findMember.get();

        kakaoLoginRepository.updateKakaoToken(social.getMember(), socialToken);

        return social.getMember();
    }

    @Override
    public String logout(Member member) {
        Social social = member.getSocial();
        SocialToken token = social.getToken();
        kakaoLoginRepository.logout(token);

        return kakaoLoginRepository.redirectLogout();
    }
}
