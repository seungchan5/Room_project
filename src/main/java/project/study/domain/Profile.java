package project.study.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "PROFILE")
public class Profile extends ImageFileEntityChildren {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public Profile(Member member, String originalName, String storeName) {
        super(originalName, storeName);
        this.member = member;
    }
}
