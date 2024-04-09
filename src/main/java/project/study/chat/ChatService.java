package project.study.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.chat.component.ChatAccessToken;
import project.study.chat.component.ChatCurrentMemberManager;
import project.study.chat.component.ChatManager;
import project.study.domain.Chat;
import project.study.chat.dto.*;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.jpaRepository.MemberJpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static project.study.chat.MessageType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ChatAccessToken chatAccessToken;
    private final ChatCurrentMemberManager currentMemberManager;
    private final ChatManager chatManager;

    public Member getMember(String token, Long roomId) {
        Long accessMemberId = chatAccessToken.getMemberId(token, roomId);
        Optional<Member> findMember = memberJpaRepository.findById(accessMemberId);
        return findMember.get();
    }

    public ChatObject<Collection<String>> changeToMemberListDto(ChatDto chat) {
        Collection<String> memberList = currentMemberManager.getMemberList(chat.getRoomId());
        return new ChatObject<>(chat, memberList);
    }

    public void accessRemove(Member member, Long roomId) {
        chatAccessToken.remove(roomId, member.getMemberId());
    }

    public void saveChat(ChatDto chat, Member member, Room room) {
        chatRepository.saveChat(chat, member, room);
    }

    public ChatObject<ResponseNextManager> exitRoom(Member member, Room room) {
        ChatDto chat = chatManager.sendMessageChatDto(member, room, EXIT, member.getMemberNickname() + "님이 방에서 나가셨습니다.");

        Optional<Member> nextManagerMember = room.getJoinRoomList()
                .stream()
                .filter(joinRoom -> !joinRoom.compareMember(member) && joinRoom.isManager())
                .map(JoinRoom::getMember)
                .findFirst();

        ResponseNextManager responseNextManager = nextManagerMember
                .map(ResponseNextManager::new)
                .orElse(new ResponseNextManager());

        return new ChatObject<>(chat, responseNextManager);
    }

    public List<String> findRecentlyHistoryMemberNickname(Long memberId, Room room) {
        return chatRepository.findRecentlyHistoryMemberNickname(memberId, room);
    }

    public List<Chat.ResponseChatHistory> findByChatHistory(Room room, Long pageValue, Long memberId) {
        List<Chat> byChatHistory = chatRepository.findByChatHistory(room, pageValue);
        byChatHistory.sort(Chat::compareTo);
        return byChatHistory.stream().map(x -> x.getResponseChatHistory(memberId)).toList();
    }
}
