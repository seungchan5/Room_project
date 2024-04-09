package project.study.chat;

public enum MessageType {
    ENTER, // 채팅방 접속
    TALK,  // 채팅 메세지
    LEAVE, // 채팅방 나감 (웹소켓에서 나감)
    UPDATE, // 채팅방 설정 변경사항 업데이트
    NOTICE, // 공지사항 변경
    KICK, // 강퇴
    ENTRUST, // 방장 위임
    REQUIRE_PHONE,
    EXIT; // 방에서 완전히 나감
}
