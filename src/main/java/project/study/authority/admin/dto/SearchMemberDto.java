package project.study.authority.admin.dto;

import lombok.*;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchMemberDto {

    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String phone;
    private String memberCreateDate;
    private int memberNotifyCount;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;

}
