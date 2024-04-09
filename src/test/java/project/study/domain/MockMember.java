package project.study.domain;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import project.study.common.CustomDateTime;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;
import project.study.jpaRepository.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MockMember {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private FileUpload fileUpload;

    private static int index = 0;
    private static final String nickname = "테스트닉네임";
    private static final String name = "테스트이름";
    private static final String account = "test";
    private static final String password = "!@#QWEasd";

    public MockMemberBuilder createMember() {
        Member saveMember = getSaveMember();
        em.persist(saveMember);

        fileUpload.saveFile(null, FileUploadType.MEMBER_PROFILE, saveMember);

        return new MockMemberBuilder(memberJpaRepository, em, saveMember);
    }

    private static Member getSaveMember() {
        return Member.builder()
            .memberNickname(nickname + index)
            .memberName(name)
            .memberStatus(MemberStatusEnum.정상)
            .memberCreateDate(CustomDateTime.now())
            .build();
    }

    public class MockMemberBuilder {

        private MemberJpaRepository memberJpaRepository;
        private EntityManager em;
        private Member member;

        public MockMemberBuilder(MemberJpaRepository memberJpaRepository, EntityManager em, Member member) {
            this.memberJpaRepository = memberJpaRepository;
            this.em = em;
            this.member = member;
        }

        public MockMemberBuilder setBasic() {

            String salt = UUID.randomUUID().toString().substring(0, 6);

            Basic saveBasic = Basic.builder()
                .member(member)
                .account(account + index)
                .password(encoder.encode(password + index + salt))
                .salt(salt)
                .build();
            em.persist(saveBasic);
            return this;
        }

        public MockMemberBuilder setSocial(SocialEnum socialType) {
            Social saveSocial = Social.builder()
                .member(member)
                .socialType(socialType)
                .socialEmail(account + index + "@naver.com")
                .build();

            em.persist(saveSocial);
            return this;
        }

        public MockMemberBuilder setFreeze(LocalDateTime endDate) {
            Freeze saveFreeze = Freeze.builder()
                .member(member)
                .freezeEndDate(endDate)
                .freezeReason("테스트를 위한 이용정지")
                .build();
            em.persist(saveFreeze);
            member.changeStatusToFreeze();
            return this;
        }

        public MockMemberBuilder setExpire() {
            member.changeStatusToExpire();
            return this;
        }

        public Member build() {
            em.flush();
            em.clear();
            index++;
            return memberJpaRepository.findById(member.getMemberId()).get();
        }

    }


}
