package project.study.authority.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.BindingResult;
import project.study.domain.Room;

@Getter
@Setter
@ToString
public class RequestEditRoomDto extends RequestCreateRoomDto {

    private boolean defaultImage;

    public void validEdit(BindingResult bindingResult, Room room) {
        validRoomEditMaxPerson(bindingResult, room.joinRoomSize(), getMax());
        valid(bindingResult, "방 설정 변경 에러");
    }

    private void validRoomEditMaxPerson(BindingResult bindingResult, int nowPerson, int max) {
        if (max < 2 || max > 6) {
            bindingResult.rejectValue("max", "인원 수를 알맞게 설정해주세요.");
            return;
        }

        if (nowPerson >max) {
            bindingResult.rejectValue("max", "참여된 회원 수보다 작게 설정할 수 없습니다.");
        }
    }
}
