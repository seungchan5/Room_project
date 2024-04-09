package project.study.authority.admin.dto;

import lombok.*;
import project.study.enums.NotifyStatus;
import project.study.enums.NotifyType;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SearchNotifyReadMoreDtoBatis {

    private String reporterMemberAccount;
    private String criminalMemberAccount;
    private Long roomId;
    private String notifyDate;
    private NotifyType notifyReason;
    private String notifyContent;
    private Long notifyId;
    private NotifyStatus notifyStatus;
    @Setter
    private List<SearchNotifyImageDtoBatis> notifyImages;

}
