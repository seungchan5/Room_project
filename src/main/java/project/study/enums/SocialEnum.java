package project.study.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialEnum {

    KAKAO("카카오"),
    NAVER("네이버");

    private String korName;
}
