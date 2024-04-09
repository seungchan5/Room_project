package project.study.authority.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.study.enums.BanEnum;
import project.study.enums.NotifyType;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestNotifyMemberFreezeDto {

    private Long memberId;
    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String phone;
    private BanEnum freezePeriod;
    private NotifyType freezeReason;

}
