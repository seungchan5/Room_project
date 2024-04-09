package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.study.controller.api.sms.FindAccount;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BASIC")
public class Basic implements MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basicId;

    private String account;
    private String password;
    private String salt;

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public boolean isValidPassword(BCryptPasswordEncoder encoder, String comparePassword) {
        String loginPassword = combineSalt(comparePassword);
        return encoder.matches(loginPassword, password);
    }

    public void changePassword(BCryptPasswordEncoder encoder, String changePassword) {
        password = encoder.encode(combineSalt(changePassword));
    }

    private String combineSalt(String encodedBeforePassword) {
        return encodedBeforePassword + salt;
    }

    @Override
    public FindAccount findAccount() {
        return new FindAccount(null, account);
    }
}
