package project.study.enums;


public enum MemberStatusEnum {

    정상,
    이용정지,
    영구정지,
    탈퇴;

    public boolean isFreezeMember() {
        return this == 이용정지 || this == 영구정지;
    }

    public boolean isExpireMember() {
        return this == 탈퇴;
    }
}
