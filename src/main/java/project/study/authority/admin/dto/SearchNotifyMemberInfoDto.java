package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.study.enums.MemberStatusEnum;
import project.study.enums.NotifyType;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchNotifyMemberInfoDto {

    private Long memberId;
    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String memberPhone;
    private String memberCreateDate;
    private int memberNotifyCount;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;
    private NotifyType notifyReason;

    @Setter
    private String memberProfile;

}
