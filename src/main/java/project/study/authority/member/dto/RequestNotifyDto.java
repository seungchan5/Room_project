package project.study.authority.member.dto;

import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import project.study.enums.NotifyType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestNotifyDto {

    @Nullable
    private List<MultipartFile> images;
    private String nickname;
    private NotifyType notifyType;
    private String notifyContent;
}
