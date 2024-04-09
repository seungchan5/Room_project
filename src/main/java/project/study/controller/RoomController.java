package project.study.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.study.authority.member.MemberAuthorizationCheck;
import project.study.authority.member.authority.ManagerAuthority;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.chat.ChatService;
import project.study.domain.Chat;
import project.study.customAnnotation.PathRoom;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.dto.abstractentity.ResponseObject;
import project.study.dto.room.*;
import project.study.exceptions.RestFulException;
import project.study.service.JoinRoomService;
import project.study.service.RoomService;

import java.util.List;

import static project.study.constant.WebConst.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/room")
public class RoomController {

    private final MemberAuthorizationCheck authorizationCheck;
    private final RoomService roomService;
    private final JoinRoomService joinRoomService;
    private final ChatService chatService;

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDto> createRoom(@SessionLogin(required = true) Member member,
                                                  @Validated @ModelAttribute RequestCreateRoomDto data,
                                                  BindingResult bindingResult,
                                                  HttpServletResponse response) {
        MemberAuthority commonMember = authorizationCheck.getMemberAuthority(response, member);

        data.valid(bindingResult, "방 생성 에러");
        Long roomId = commonMember.createRoom(member, data);

        String redirectURI = "/room/" + roomId;
        return ResponseEntity.ok(new ResponseObject<>("방 생성 완료", redirectURI));
    }

    @GetMapping("/{room}/edit")
    public ResponseEntity<ResponseDto> getEditRoomForm(@SessionLogin(required = true) Member member,
                                                       @PathRoom("room") Room room,
                                                       HttpServletResponse response) {
        authorizationCheck.getManagerAuthority(response, member, room);
        ResponseEditRoomForm editRoomForm = roomService.getEditRoomForm(room);

        return ResponseEntity.ok(new ResponseObject<>("조회성공", editRoomForm));
    }

    @PostMapping("/{room}/edit")
    public ResponseEntity<ResponseDto> editRoom(@SessionLogin(required = true) Member member,
                                                @PathRoom("room") Room room,
                                                HttpServletResponse response,
                                                @Validated @ModelAttribute RequestEditRoomDto data,
                                                BindingResult bindingResult) {
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);

        data.validEdit(bindingResult, room);
        managerMember.editRoom(room, data);

        return ResponseEntity.ok(new ResponseDto("성공"));
    }



    @PostMapping("/{room}/private")
    public ResponseEntity<ResponseDto> roomPrivate(@SessionLogin(required = true) Member member,
                                                   @PathRoom("room") Room room,
                                                   @RequestBody String password,
                                                   HttpServletResponse response) {

        if (room.isPublic() || !room.hasRoomPassword()) return ResponseEntity.badRequest().body(new ResponseDto(ERROR, "잘못된 접근입니다."));

        if (!room.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(new ResponseDto("invalidPassword", "비밀번호가 일치하지 않습니다."));
        }
        MemberAuthority commonMember = authorizationCheck.getMemberAuthority(response, member);
        commonMember.joinRoom(new RequestJoinRoomDto(member, room, response, password));

        return ResponseEntity.ok(new ResponseDto("/room/" + room.getRoomId()));
    }

    @GetMapping("/{room}/history")
    public ResponseEntity<ResponseDto> chatHistory(@PathRoom("room") Room room,
                                                   @RequestParam(name = "token") String token,
                                                   @RequestParam(name = "page", required = false, defaultValue = "0") Long pageValue ) {
        Member member = chatService.getMember(token, room.getRoomId());

        List<Chat.ResponseChatHistory> history = chatService.findByChatHistory(room, pageValue, member.getMemberId());
        return ResponseEntity.ok(new ResponseObject<>("성공", history));
    }

    @GetMapping("/{room}/notice")
    public ResponseEntity<ResponseDto> roomNotice(@SessionAttribute(name = LOGIN_MEMBER, required = false) Long memberId, @PathRoom("room") Room room) {

        boolean exitsByMemberAndRoom = joinRoomService.exitsByMemberAndRoom(memberId, room);
        if (!exitsByMemberAndRoom) throw new RestFulException(new ResponseDto(ERROR, "권한 없음"));

        RoomNotice.ResponseRoomNotice responseRoomNotice = room.getChatInsideNotice();

        return ResponseEntity.ok(new ResponseObject<>("성공", responseRoomNotice));
    }

    @GetMapping("/{room}/memberList")
    public ResponseEntity<ResponseDto> roomMemberList(@SessionAttribute(name = LOGIN_MEMBER, required = false) Long memberId, @PathRoom("room") Room room) {

        boolean exitsByMemberAndRoom = joinRoomService.exitsByMemberAndRoom(memberId, room);
        if (!exitsByMemberAndRoom) throw new RestFulException(new ResponseDto(ERROR, "권한 없음"));

        List<String> responseRoomMemberList = chatService.findRecentlyHistoryMemberNickname(memberId, room);
        return ResponseEntity.ok(new ResponseObject<>("성공", responseRoomMemberList));
    }


}
