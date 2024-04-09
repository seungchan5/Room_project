package project.study.authority.member.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import project.study.enums.PublicEnum;
import project.study.exceptions.BindingResultException;

import java.util.List;

@Getter
@Setter
@ToString
public class RequestCreateRoomDto {

    @Nullable
    private MultipartFile image;

    @Length(min = 2, max = 10, message = "방 제목은 2~10자 이하만 가능합니다.")
    private String title;

    @Size(max = 50, message = "소개글은 50자 까지 가능합니다.")
    private String intro;

    @Min(value = 2, message = "인원 수는 2~6명으로 설정해주세요.")
    @Max(value = 6, message = "인원 수는 2~6명으로 설정해주세요.")
    private int max;

    @Size(max = 5, message = "태그는 최대 5개까지 가능합니다.")
    private List<String> tags;

    private PublicEnum roomPublic;

    @Nullable
    private String roomPassword;


    public void valid(BindingResult bindingResult, String alertMessage) {
        validPublic(bindingResult);

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(alertMessage, bindingResult);
        }
    }

    private void validPublic(BindingResult bindingResult) {
        if (roomPublic == null) {
            bindingResult.rejectValue("roomPublic", "공개 여부를 설정해주세요.");
            return;
        }
        if (roomPublic.isPublic() && roomPassword != null) {
            bindingResult.rejectValue("roomPublic", "공개방은 비밀번호를 설정할 수 없습니다.");
            return;
        }
        if (!roomPublic.isPublic() && roomPassword == null) {
            bindingResult.rejectValue("roomPublic", "비공개방은 비밀번호를 설정해야합니다.");
            return;
        }
        if (roomPublic.isPublic()) return;

        if (roomPassword == null || roomPassword.length() < 4 || roomPassword.length() > 6) {
            bindingResult.rejectValue("roomPassword", "NotFoundPassword", "비밀번호 4~6자리를 입력해주세요.");
        }
    }
}
