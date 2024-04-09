package project.study.authority.member.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRoomListDto {

    private Long roomId; // 방번호
    private String roomImage; // 방대표이미지
    private String roomTitle; // 방 제목
    private String roomIntro; // 방 소개글
    private boolean roomPublic; // 공개방 : true, 비공개방 : false
    private boolean roomJoin; // 내가 참여중인방 true, 참여중이 아닌방 false, 검색결과에만 CSS 적용됨
    private Integer nowPerson;
    private Integer maxPerson;
    private List<String> tagList; // {"태그명", "태그명", "태그명", ... }

}
