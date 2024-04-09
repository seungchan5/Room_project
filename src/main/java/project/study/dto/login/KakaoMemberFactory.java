package project.study.dto.login;

import lombok.RequiredArgsConstructor;
import project.study.controller.api.kakaologin.KakaoLoginRepository;
import project.study.controller.image.FileUpload;
import project.study.dto.login.validator.MemberValidator;
import project.study.dto.login.validator.SocialMemberValidator;
import project.study.jpaRepository.*;
import project.study.repository.FreezeRepository;

@RequiredArgsConstructor
public class KakaoMemberFactory implements MemberFactory{

    private final FreezeRepository freezeRepository;
    private final KakaoLoginRepository kakaoLoginRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final SocialJpaRepository socialJpaRepository;
    private final SocialTokenJpaRepository socialTokenJpaRepository;
    private final FileUpload fileUpload;
    @Override
    public MemberInterface createMember() {
        return new KakaoMember(kakaoLoginRepository, memberJpaRepository, socialJpaRepository, socialTokenJpaRepository);
    }

    @Override
    public MemberValidator validator() {
        return new SocialMemberValidator(freezeRepository, socialJpaRepository, memberJpaRepository);
    }

    @Override
    public FileUpload getFileUpload() {
        return fileUpload;
    }

}
