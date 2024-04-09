package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.enums.AuthorityAdminEnum;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ADMIN")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    private String account;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private AuthorityAdminEnum adminEnum;

    public boolean isOverall() {
        return adminEnum.equals(AuthorityAdminEnum.최고관리자);
    }

    public boolean isReport() {
        return adminEnum.equals(AuthorityAdminEnum.신고담당관리자) || isOverall();
    }

    public boolean isMatchesPassword(String password) {
        return this.password.equals(password);
    }


}


