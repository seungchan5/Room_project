package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.controller.api.sms.FindAccount;
import project.study.enums.SocialEnum;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SOCIAL")
public class Social implements MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialId;

    @Getter
    @Enumerated(EnumType.STRING)
    private SocialEnum socialType;

    private String socialEmail;

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(mappedBy = "social")
    private SocialToken socialToken;

    public SocialToken getToken() {
        return socialToken;
    }

    @Override
    public FindAccount findAccount() {
        return new FindAccount(socialType, socialEmail);
    }

    public boolean isEqualsSocialType(SocialEnum socialType) {
        return this.socialType.equals(socialType);
    }
}
