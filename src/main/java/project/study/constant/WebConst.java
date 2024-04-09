package project.study.constant;

public abstract class WebConst {

    // ResponseDto result
    public static final String OK = "ok";
    public static final String ERROR = "error";
    public static final String NOT_LOGIN = "notLogin";
    public static final String LOGIN_MEMBER = "memberId";
    public static final String REQUIRE_LOGIN = "requireLogin";

    public static final int MAX_CREATE_ROOM_COUNT = 5;
    public static final int MAX_JOIN_ROOM_COUNT = 5;

    public static final String EXPIRE_MEMBER_PROFILE = "basic-member-profile.jpg";
    public static final String DEFAULT_PROFILE = "basic-member-profile.jpg";
    public static final String DEFAULT_ROOM_IMAGE = "basic-room-profile.jpg";

    public static final int EXPIRE_PLUS_DAY = 14;
}
