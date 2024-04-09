package project.study.authority.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchExpireMemberDto {

    private String memberAccount;
    private String memberName;
    private String memberNickname;
    private String memberPhone;
    private String memberCreateDate;
    private String memberExpireDate;
    private SocialEnum socialType;
    private MemberStatusEnum memberStatusEnum;

}
