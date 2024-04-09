package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.chat.ChatService;
import project.study.chat.component.ChatManager;
import project.study.chat.dto.ChatDto;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.exceptions.NotLoginMemberRestException;
import project.study.exceptions.authority.NotJoinRoomException;
import project.study.exceptions.authority.joinroom.ExceedJoinRoomException;
import project.study.repository.JoinRoomRepository;
import project.study.repository.RoomRepository;

import java.util.Optional;

import static project.study.chat.MessageType.ENTRUST;
import static project.study.enums.AuthorityMemberEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinRoomService {

    private final SimpMessageSendingOperations template;
    private final RoomRepository roomRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final ChatService chatService;
    private final ChatManager chatManager;

    public boolean exitsByMemberAndRoom(Long memberId, Room room) {
        if (memberId == null) throw new NotLoginMemberRestException();
        return joinRoomRepository.exitsByMemberAndRoom(memberId, room);
    }

    public void validMaxJoinRoom(Member member, HttpServletResponse response) {
        if (member.isExceedJoinRoom(일반)) {
            throw new ExceedJoinRoomException(response);
        }
    }

    public synchronized JoinRoom joinRoom(RequestJoinRoomDto data) {
        JoinRoom saveJoinRoom = data.saveJoinRoom();
        return joinRoomRepository.save(saveJoinRoom);
    }

    public JoinRoom findByMemberAndRoom(Member member, Room room, HttpServletResponse response) {
        Optional<JoinRoom> findJoinRoom = joinRoomRepository.findByMemberAndRoom(member, room);
        return findJoinRoom.orElseThrow(() -> new NotJoinRoomException(response));
    }

    public Optional<JoinRoom> findByMemberAndRoom(Member member, Room room) {
        return joinRoomRepository.findByMemberAndRoom(member, room);
    }

    public void exitRoom(JoinRoom joinRoom) {
        Room room = joinRoom.getRoom();
        Optional<JoinRoom> anotherJoinMember = room.getJoinRoomList().stream().filter(innerJoinRoom -> !innerJoinRoom.equals(joinRoom)).findAny();

        if (joinRoom.isManager()) {
            anotherJoinMember.ifPresentOrElse(
                anotherMember -> {
                    anotherMember.changeToAuthority(방장);
                    ChatDto chat = chatManager.sendMessageChatDto(anotherMember.getMember(), room, ENTRUST, anotherMember.getMember().getMemberNickname() + "님이 방장이 되셨습니다.");
                    templateSendMessage(room.getRoomId(), chat);
                }, // 다른회원에게 방장 위임
                () -> roomRepository.moveToDeleteRoom(room)); // 다른 회원이 없는경우 (참여자가 1명인 경우) 방 삭제
        }

        chatService.accessRemove(joinRoom.getMember(), room.getRoomId());
        templateSendMessage(room.getRoomId(), chatService.exitRoom(joinRoom.getMember(), room));
        joinRoomRepository.deleteJoinRoom(joinRoom);
    }

    private void templateSendMessage(Long roomId, ChatDto chat) {
        template.convertAndSend("/sub/chat/room/" + roomId, chat);
    }
}
