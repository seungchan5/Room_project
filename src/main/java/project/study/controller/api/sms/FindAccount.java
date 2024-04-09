package project.study.controller.api.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.study.enums.SocialEnum;

@Getter
@AllArgsConstructor
public class FindAccount {

    private SocialEnum social;
    private String account;
}
