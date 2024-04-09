package project.study.authority.admin.dto;

import lombok.*;
import project.study.enums.NotifyStatus;
import project.study.enums.NotifyType;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchNotifyDto {

    private String reporterMemberAccount;
    private String criminalMemberAccount;
    private Long roomId;
    private String notifyDate;
    private NotifyType notifyReason;
    private Long notifyId;
    private NotifyStatus notifyStatus;

}
