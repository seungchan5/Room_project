package project.study.chat.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.domain.Member;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChatCurrentMemberManager {

    private final Map<Long, List<Long>> currentChatMember = new HashMap<>();
    private final Map<Long, String> memberNicknameMap = new HashMap<>();

    public void plus(Long roomId, Member member) {
        if (!currentChatMember.containsKey(roomId)) {
            currentChatMember.put(roomId, new LinkedList<>());
        }
        Long memberId = member.getMemberId();
        List<Long> memberList = currentChatMember.get(roomId);

        if (memberList.contains(memberId)) return;
        memberList.add(memberId);
        memberNicknameMap.put(memberId, member.getMemberNickname());
    }
    public void minus(Long roomId, Long memberId) {
        List<Long> memberList = currentChatMember.get(roomId);
        memberNicknameMap.remove(memberId);

        if (memberList == null) return;
        memberList.remove(memberId);

        if (memberList.isEmpty()) {
            currentChatMember.remove(roomId);
        }
    }

    public Collection<String> getMemberList(Long roomId) {
        System.out.println("currentChatMember = " + currentChatMember.get(roomId));

        List<Long> memberList = currentChatMember.get(roomId);
        List<String> nicknameList = new ArrayList<>();
        for (Long memberId : memberList) {
            nicknameList.add(memberNicknameMap.get(memberId));
        }
        return nicknameList;
    }

    public Long getRoomId(Long memberId, Long roomId) {
        List<Long> memberList = currentChatMember.get(roomId);
        if (!memberList.contains(memberId)) return null;
        return roomId;
    }

    public boolean containsInRoom(Long roomId, Long memberId) {
        List<Long> chatMemberList = currentChatMember.getOrDefault(roomId, new ArrayList<>());
        return chatMemberList.contains(memberId);
    }
}
