package project.study.dto.mypage;

import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestEditInfoDto {

    @Nullable
    private MultipartFile profile;
    private String name;
    private String nickname;

}
