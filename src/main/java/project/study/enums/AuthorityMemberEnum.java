package project.study.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthorityMemberEnum {

    방장,
    일반;


    public boolean isManager() {
        return this == 방장;
    }
}
