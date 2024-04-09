package project.study.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SOCIAL_TOKEN")
@NoArgsConstructor
public class SocialToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialTokenId;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOCIAL_ID")
    private Social social;

    @Getter @Setter
    private String access_token;
    @Getter @Setter
    private String refresh_token;

    public SocialToken(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

}
