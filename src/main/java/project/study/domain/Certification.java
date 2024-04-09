package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.common.CustomDateTime;
import project.study.constant.WebConst;
import project.study.controller.api.sms.RequestSms;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.sms.ExceedExpireException;
import project.study.exceptions.sms.SmsException;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CERTIFICATION")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;
    private String name;
    @Getter
    private String phone;
    private String certificationNumber;
    private LocalDateTime expireDate;

    public void valid(RequestSms data) throws ExceedExpireException {
        if (CustomDateTime.now().isAfter(expireDate)) {
            throw new ExceedExpireException();
        }
        if (!name.equals(data.getName()) || !phone.equals(data.getPhone()) || !certificationNumber.equals(data.getCertification())) {
            throw new SmsException(new ResponseDto("error", "인증에 실패했습니다."));
        }
    }

    public void changePasswordValid(RequestSms data) throws ExceedExpireException {
        if (CustomDateTime.now().isAfter(expireDate)) {
            throw new ExceedExpireException();
        }
        if (!phone.equals(data.getPhone()) || !certificationNumber.equals(data.getCertification())) {
            throw new SmsException(new ResponseDto(WebConst.ERROR, "인증에 실패했습니다."));
        }
    }
}
