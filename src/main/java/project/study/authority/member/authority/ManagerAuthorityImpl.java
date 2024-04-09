package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.*;
import project.study.constant.WebConst;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;
import project.study.repository.MemberRepository;
import project.study.service.RoomService;

import java.util.Optional;

import static project.study.enums.AuthorityMemberEnum.*;

@Component
@Transactional
@RequiredArgsConstructor
public class ManagerAuthorityImpl implements ManagerAuthority{

    private final RoomService roomService;
    private final MemberRepository memberRepository;

    @Override
    public void editRoom(Room room, RequestEditRoomDto data) {
        roomService.editRoom(room, data);
    }

    @Override
    public Member managerEntrust(Member member, Room room, RequestEntrustDto data) {
        Member nextManagerMember = memberRepository.findByMemberNickname(data.getNickname());

        JoinRoom currentManager = findByJoinRoomMember(room, member, "참여자가 아닙니다.");
        JoinRoom nextManager = findByJoinRoomMember(room, nextManagerMember, "권한이 없습니다.");

        currentManager.changeToAuthority(일반);
        nextManager.changeToAuthority(방장);

        return nextManagerMember;
    }

    @Override
    public RoomNotice.ResponseRoomNotice uploadNotice(Room room, RoomNotice.RequestNoticeDto data) {

        data.validNotice();

        if (!room.hasNotice()) {
            RoomNotice saveRoomNotice = roomService.saveRoomNotice(room, data);
            return saveRoomNotice.buildResponseRoomNotice();
        }
        room.updateNotice(data.getNotice());
        return room.getChatInsideNotice();
    }

    @Override
    public void deleteNotice(Room room) {
        if (room.hasNotice()) {
            roomService.deleteRoomNotice(room);
        }
    }

    @Override
    public Member kickMember(HttpServletResponse response, Room room, RequestKickDto data) {
        Member kickMember = memberRepository.findByMemberNickname(data.getNickname());

        JoinRoom joinRoom = findByJoinRoomMember(room, kickMember, "참여자가 아닙니다.");

        roomService.deleteJoinRoom(joinRoom);

        return kickMember;
    }

    @NotNull
    private JoinRoom findByJoinRoomMember(Room room, Member member, String errorMessage) {
        Optional<JoinRoom> findJoinRoom = room.getJoinRoomList().stream().filter(joinRoom -> joinRoom.compareMember(member)).findFirst();
        return findJoinRoom.orElseThrow(() -> new RestFulException(new ResponseDto(WebConst.ERROR, errorMessage)));
    }
    
}
