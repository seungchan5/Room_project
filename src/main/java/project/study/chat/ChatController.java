package project.study.chat;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import project.study.authority.member.MemberAuthorizationCheck;
import project.study.authority.member.authority.ManagerAuthority;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.dto.RequestEntrustDto;
import project.study.authority.member.dto.RequestKickDto;
import project.study.chat.component.ChatAccessToken;
import project.study.chat.component.ChatManager;
import project.study.chat.dto.*;
import project.study.common.CustomDateTime;
import project.study.customAnnotation.PathRoom;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;
import project.study.service.JoinRoomService;
import project.study.service.RoomService;

import java.util.Optional;

import static project.study.chat.MessageType.ENTRUST;
import static project.study.chat.MessageType.KICK;
import static project.study.constant.WebConst.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatService chatService;

    private final MemberAuthorizationCheck authorizationCheck;
    private final RoomService roomService;
    private final ChatAccessToken chatAccessToken;
    private final ChatManager chatManager;
    private final JoinRoomService joinRoomService;


    @ResponseBody
    @GetMapping("/room/{room}/access")
    public ResponseEntity<ResponseDto> accessToken(@SessionLogin Member member, @PathRoom("room") Room room) {
        boolean exitsByMemberAndRoom = joinRoomService.exitsByMemberAndRoom(member == null ? null : member.getMemberId(), room);
        if (!exitsByMemberAndRoom) throw new RestFulException(new ResponseDto(ERROR, "권한 없음"));

        String accessToken = chatAccessToken.createAccessToken(member, room.getRoomId());
        return ResponseEntity.ok(new ResponseDto(accessToken));
    }

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        Member member  = chatService.getMember(chat.getToken(), chat.getRoomId());

        boolean hasPhone = member.hasPhone();

        headerAccessor.getSessionAttributes().put("member", member);
        headerAccessor.getSessionAttributes().put("hasPhone", hasPhone);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());

        chat.setSenderImage(member.getStoreImage());
        chat.setSender(member.getMemberNickname());
        chat.setMessage(member.getMemberNickname() + "님이 입장하셨습니다.");
        chat.setTime(CustomDateTime.now());

        templateSendMessage(chat.getRoomId(), chatService.changeToMemberListDto(chat));

        if (!hasPhone) {
            ChatDto requirePhone = ChatDto.builder().type(MessageType.REQUIRE_PHONE).build();
            templateSendMessage(chat.getRoomId(), requirePhone);
        }
    }

    @MessageMapping("/chat/update")
    public void roomUpdate(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
        Optional<Room> findRoom = roomService.findById(roomId);
        if (findRoom.isEmpty()) return;
        Room room = findRoom.get();

        Room.ResponseRoomUpdateInfo roomInfo = room.getResponseRoomUpdateInfo();

        chat.setMessage("방 설정이 변경되었습니다.");
        chat.setTime(CustomDateTime.now());

        templateSendMessage(roomId, new ChatObject<>(chat, roomInfo));

    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        boolean hasPhone = (boolean) headerAccessor.getSessionAttributes().get("hasPhone");
        if (!hasPhone) {
            return;
        }

        Member member = (Member) headerAccessor.getSessionAttributes().get("member");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
        Optional<Room> findRoom = roomService.findById(roomId);
        boolean checkJoinRoom = chatAccessToken.hasJoinRoom(chat);
        if (findRoom.isEmpty() || !checkJoinRoom) return;
        Room room = findRoom.get();

        chat.setSenderImage(member.getStoreImage());
        chat.setSender(member.getMemberNickname());
        chat.setMessage(chat.getMessage());
        chat.setTime(CustomDateTime.now());

        chatService.saveChat(chat, member, room);

        templateSendMessage(roomId, chat);

    }

    @ResponseBody
    @PostMapping(value = "/room/exit")
    public ResponseEntity<ResponseDto> exitRoom(@SessionLogin(required = true) Member member, @RequestBody String roomId, HttpServletResponse response) {
        Room room = roomService.findByRoom(roomId, response);

        MemberAuthority commonMember = authorizationCheck.getMemberAuthority(response, member);
        commonMember.exitRoom(member, room, response);

        chatService.accessRemove(member, room.getRoomId());

        return ResponseEntity.ok(new ResponseDto("방 탈퇴 완료"));
    }


    // 회원 강퇴
    @ResponseBody
    @DeleteMapping("/room/{room}/kick")
    public ResponseEntity<ResponseDto> roomKick(@SessionLogin(required = true) Member member,
                                                @PathRoom("room") Room room,
                                                HttpServletResponse response,
                                                @RequestBody RequestKickDto data) {
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);
        Member kickMember = managerMember.kickMember(response, room, data);

        chatService.accessRemove(kickMember, room.getRoomId());

        ChatDto kick = chatManager.sendMessageChatDto(kickMember, room, KICK, kickMember.getMemberNickname() + "님이 강퇴당했습니다.");
        templateSendMessage(room.getRoomId(), kick);

        return ResponseEntity.ok(new ResponseDto("성공"));
    }
    // 매니저 위임
    @ResponseBody
    @PostMapping("/room/{room}/entrust")
    public ResponseEntity<ResponseDto> roomManagerEntrust(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                            HttpServletResponse response,
                                            @RequestBody RequestEntrustDto data) {
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);
        Member nextManager = managerMember.managerEntrust(member, room, data);

        ChatDto chat = chatManager.sendMessageChatDto(nextManager, room, ENTRUST, nextManager.getMemberNickname() + "님이 방장이 되셨습니다.");

        templateSendMessage(room.getRoomId(), chat);
        return ResponseEntity.ok(new ResponseDto("성공"));
    }
    // 공지사항 등록 및 수정
    @ResponseBody
    @PostMapping("/room/{room}/notice")
    public ResponseEntity<ResponseDto> roomUploadNotice(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                            HttpServletResponse response,
                                            @RequestBody RoomNotice.RequestNoticeDto data) {
        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);

        RoomNotice.ResponseRoomNotice roomNotice = managerMember.uploadNotice(room, data);

        ChatDto chat = chatManager.sendMessageChatDto(member, room, MessageType.NOTICE, "공지사항이 등록되었습니다.");

        templateSendMessage(room.getRoomId(), new ChatObject<>(chat, roomNotice));
        return ResponseEntity.ok(new ResponseDto("성공"));
    }

    // 공지사항 삭제
    @ResponseBody
    @DeleteMapping("/room/{room}/notice/delete")
    public ResponseEntity<ResponseDto> roomDeleteNotice(@SessionLogin(required = true) Member member, @PathRoom("room") Room room,
                                                        HttpServletResponse response) {

        ManagerAuthority managerMember = authorizationCheck.getManagerAuthority(response, member, room);

        if (!room.hasNotice()) {
            return ResponseEntity.ok(new ResponseDto(ERROR, "공지사항이 존재하지 않습니다."));
        }

        managerMember.deleteNotice(room);

        ChatDto chat = chatManager.sendMessageChatDto(member, room, MessageType.NOTICE, "공지사항이 삭제되었습니다.");

        templateSendMessage(room.getRoomId(), chat);

        return ResponseEntity.ok(new ResponseDto("성공"));
    }


    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        Member member = (Member) headerAccessor.getSessionAttributes().get("member");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
        Optional<Room> findRoom = roomService.findById(roomId);
        if (findRoom.isEmpty()) return;
        Room room = findRoom.get();

        chatService.accessRemove(member, room.getRoomId());

        log.info("headAccessor {}", headerAccessor);

        ChatDto chat = chatManager.sendMessageChatDto(member, room, MessageType.LEAVE, member.getMemberNickname() + "님이 채팅방에서 나가셨습니다.");

        templateSendMessage(room.getRoomId(), chatService.changeToMemberListDto(chat));
    }

    private void templateSendMessage(Long roomId, ChatDto chat) {
        template.convertAndSend("/sub/chat/room/" + roomId, chat);
    }
}
