package project.study.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BanEnum {

    B1(1),
    B3(3),
    B7(7),
    B30(30),
    B365(365),
    BPermanent(9999);

    private int banEnum;
}
