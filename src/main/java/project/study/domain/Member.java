package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.common.CustomDateTime;
import project.study.controller.api.sms.FindAccount;
import project.study.dto.MyPageInfo;
import project.study.dto.mypage.RequestEditInfoDto;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.MemberStatusEnum;
import project.study.enums.SocialEnum;

import java.time.LocalDateTime;
import java.util.List;

import static project.study.constant.WebConst.*;
import static project.study.enums.MemberStatusEnum.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "MEMBER")
public class Member implements ImageFileEntity {

    @Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String memberName;
    @Getter
    private String memberNickname;
    private LocalDateTime memberCreateDate;
    @Enumerated(EnumType.STRING)
    private MemberStatusEnum memberStatus;
    private int memberNotifyCount;
    private LocalDateTime memberExpireDate;
    @Getter
    private String phone;

    // Not Columns
    @Getter
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Social social;
    @Getter
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Basic basic;
    @Getter
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Profile profile;
    @Getter
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Freeze> freeze;

    @Getter
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<JoinRoom> joinRoomList;

    public void changeStatusToNormal() {
        memberStatus = 정상;
    }
    public void changeStatusToExpire() {
        this.memberStatus = 탈퇴;
        this.memberExpireDate = CustomDateTime.now().plusDays(EXPIRE_PLUS_DAY);
//        this.memberExpireDate = LocalDateTime.now();
    }
    public void changeStatusToFreeze() {
        this.memberStatus = 이용정지;
    }
    public boolean isFreezeMember() {
        return memberStatus.isFreezeMember();
    }

    public boolean isExpireMember() {
        return memberStatus.isExpireMember();
    }

    public boolean isSocialMember() {
        return social != null;
    }

    public boolean isBasicMember() {
        return basic != null;
    }

    public boolean isOutOfExpireDate() {
        return memberExpireDate.isBefore(CustomDateTime.now());
    }

    @Override
    public String getStoreImage() {
        if (profile == null) return "";
        if (isExpireMember()) return EXPIRE_MEMBER_PROFILE;
        return profile.getStoreName();
    }

    @Override
    public boolean isDefaultImage() {
        return DEFAULT_PROFILE.equals(getStoreImage());
    }

    @Override
    public void setImage(String originalName, String storeName) {
        if (profile == null) return;
        profile.setImage(originalName, storeName);
    }

    public void updateInfo(RequestEditInfoDto data) {
        memberName = data.getName();
        memberNickname = data.getNickname();
    }

    public int joinRoomCount(AuthorityMemberEnum authorityEnum) {
        return (int) joinRoomList.stream().filter(joinRoom -> !joinRoom.compareAuthority(authorityEnum)).count();
    }

    public MyPageInfo getMyPageInfo() {
        return MyPageInfo.builder()
            .profile(getStoreImage())
            .name(memberName)
            .nickname(memberNickname)
            .phone(phone)
            .isSocial(isSocialMember())
            .build();
    }

    public FindAccount findAccount() {
        if (isBasicMember()) {
            return basic.findAccount();
        } else if (isSocialMember()) {
            return social.findAccount();
        }
        return new FindAccount(null, "다시 시도해주세요.");
    }

    public boolean isKakao() {
        if (!isSocialMember()) return false;
        return social.isEqualsSocialType(SocialEnum.KAKAO);
    }

    public boolean isExceedJoinRoom(AuthorityMemberEnum authority) {
        return joinRoomCount(authority) >= MAX_JOIN_ROOM_COUNT;
    }


    public boolean hasPhone() {
        return this.phone != null;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }
}
