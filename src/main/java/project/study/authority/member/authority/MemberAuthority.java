package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.authority.member.dto.RequestNotifyDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;

import java.util.List;

public interface MemberAuthority {

    Long createRoom(Member member, RequestCreateRoomDto data);
    void notify(Member member, Room room, RequestNotifyDto data);
    JoinRoom joinRoom(RequestJoinRoomDto data);
    List<ResponseRoomListDto> getMyRoomList(Member member);
    void exitRoom(Member member, Room room, HttpServletResponse response);
}
