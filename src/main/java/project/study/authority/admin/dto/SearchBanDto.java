package project.study.authority.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchBanDto {

    private Long banId;
    private Long memberId;
    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String phone;
    private String suspendedDate;
}
