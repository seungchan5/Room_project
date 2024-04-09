package project.study.authority.member.dto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.exceptions.authority.joinroom.FullRoomException;

import static project.study.enums.AuthorityMemberEnum.일반;

@Getter
@Setter
@AllArgsConstructor
public class RequestJoinRoomDto {

    private final Member member;
    private final Room room;
    private final HttpServletResponse response;
    private String password;

    public JoinRoom saveJoinRoom() {
        validFullRoom();

        return JoinRoom.builder()
                .room(room)
                .member(member)
                .authorityEnum(일반)
                .build();
    }

    public void validFullRoom() {
        if (room.isFullRoom()) throw new FullRoomException(response, "방이 가득찼습니다.");
    }
}
