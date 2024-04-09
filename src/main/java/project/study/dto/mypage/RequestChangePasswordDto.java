package project.study.dto.mypage;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestChangePasswordDto {

    private String nowPassword;
    private String changePassword;
    private String checkPassword;
}
