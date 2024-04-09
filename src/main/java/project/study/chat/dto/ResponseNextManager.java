package project.study.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.domain.Member;

@Getter
@NoArgsConstructor
public class ResponseNextManager {

    private String nextManager = "";
    private Long token = 0L;

    public ResponseNextManager(Member member) {
        this.nextManager = member.getMemberNickname();
        this.token = member.getMemberId();
    }

}