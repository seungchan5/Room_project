package project.study.authority.member.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.authority.member.dto.RequestNotifyDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Notify;
import project.study.domain.Room;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;
import project.study.exceptions.authority.joinroom.InvalidPublicPasswordException;
import project.study.service.JoinRoomService;
import project.study.service.NotifyService;
import project.study.service.RoomService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import static project.study.constant.WebConst.*;


@RequiredArgsConstructor
@Component
@Transactional
public class MemberAuthorityImpl implements MemberAuthority{

    private final RoomService roomService;
    private final JoinRoomService joinRoomService;
    private final NotifyService notifyService;

    @Override
    public Long createRoom(Member member, RequestCreateRoomDto data) {
        roomService.validMaxCreateRoom(member);
        return roomService.createRoom(data, member);
    }

    @Override
    public void notify(Member member, Room room, RequestNotifyDto data) {
        validNotify(data);
        Notify saveNotify = notifyService.saveNotify(member, room, data);

        notifyService.saveNotifyImage(saveNotify, data);

    }

    private void validNotify(RequestNotifyDto data) {
        String nickname = data.getNickname();
        if (nickname == null) {
            throw new RestFulException(new ResponseDto(ERROR, "신고하는 유저를 선택해주세요."));
        }
        String content = data.getNotifyContent();
        if (content == null || content.length() > 1000) {
            throw new RestFulException(new ResponseDto(ERROR, "신고내용을 1000자 이내로 작성해주세요."));
        }
    }

    @Override
    public List<ResponseRoomListDto> getMyRoomList(Member member) {
        return roomService.getMyRoomList(member);
    }

    @Override
    public JoinRoom joinRoom(RequestJoinRoomDto data) {
        Member member = data.getMember();
        Room room = data.getRoom();
        HttpServletResponse response = data.getResponse();
        String password = data.getPassword();

        // 이미 참여한 회원인지 확인
        Optional<JoinRoom> findJoinRoom = joinRoomService.findByMemberAndRoom(member, room);
        if (findJoinRoom.isPresent()) return findJoinRoom.get();

        // 멤버의 최대 참여수 확인
        joinRoomService.validMaxJoinRoom(member, response);

        // 비공개방인지 확인
        if (!room.isPublic()) {
            if (password == null) execute(response, room.getRoomId());

            if (!room.hasRoomPassword()) throw new InvalidPublicPasswordException(response, "문제가 생겼습니다. 관리자에게 문의해주세요.");

            if (!room.isValidPassword(password)) throw new InvalidPublicPasswordException(response, "비밀번호가 일치하지 않습니다.");
        }

        return joinRoomService.joinRoom(data);
    }

    private String execute(HttpServletResponse response, Long roomId) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> window.location.href='/room/" + roomId+ "/private'; </script>";
        try (PrintWriter out = response.getWriter()) {
            out.println(command);
            out.flush();
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public void exitRoom(Member member, Room room, HttpServletResponse response) {
        // 참가자인지 확인
        JoinRoom joinRoom = joinRoomService.findByMemberAndRoom(member, room, response);

        joinRoomService.exitRoom(joinRoom);
    }
}
