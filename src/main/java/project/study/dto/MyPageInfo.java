package project.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MyPageInfo {

    private String profile;
    private String name;
    private String nickname;
    private String phone;
    private boolean isSocial;
}
