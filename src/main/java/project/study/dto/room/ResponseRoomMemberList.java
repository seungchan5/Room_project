package project.study.dto.room;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ResponseRoomMemberList {

    private Long memberId;
    private String image;
    private String name;
    private boolean isManager;
    private boolean isMe;

}
