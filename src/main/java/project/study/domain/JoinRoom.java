package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.enums.AuthorityMemberEnum;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "JOIN_ROOM")
public class JoinRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long joinRoomId;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Enumerated(EnumType.STRING)
    private AuthorityMemberEnum authorityEnum;

    public void changeToAuthority(AuthorityMemberEnum authority) {
        this.authorityEnum = authority;
    }

    public boolean isManager() {
        System.out.println("authorityEnum = " + authorityEnum);
        return this.authorityEnum.isManager();
    }

    public boolean compareAuthority(AuthorityMemberEnum authorityEnum) {
        return this.authorityEnum.equals(authorityEnum);
    }

    public boolean compareMember(Member member) {
        return this.member.equals(member);
    }

    public ResponseRoomListDto getResponseRoomListDto() {
        return room.getResponseRoomListDto(null);
    }
}
