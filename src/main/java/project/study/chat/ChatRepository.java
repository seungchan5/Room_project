package project.study.chat;

import project.study.domain.Chat;
import project.study.chat.dto.ChatDto;
import project.study.domain.Member;
import project.study.domain.Room;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    void saveChat(ChatDto chat, Member member, Room room);

    List<String> findRecentlyHistoryMemberNickname(Long memberId, Room room);

    List<Chat> findByChatHistory(Room room, Long pageValue);

    Optional<Member> findByMemberNickname(Room room, String nickname);
}
