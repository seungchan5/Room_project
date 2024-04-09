package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import project.study.authority.member.dto.*;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.domain.RoomNotice;

public interface ManagerAuthority {

    void editRoom(Room room, RequestEditRoomDto data);
    Member managerEntrust(Member member, Room room, RequestEntrustDto data);
    RoomNotice.ResponseRoomNotice uploadNotice(Room room, RoomNotice.RequestNoticeDto data);
    void deleteNotice(Room room);
    Member kickMember(HttpServletResponse response, Room room, RequestKickDto data);
}
