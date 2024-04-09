package project.study.controller.api.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.common.CustomDateTime;
import project.study.domain.*;
import project.study.dto.abstractentity.ResponseDto;
import project.study.enums.SocialEnum;
import project.study.exceptions.sms.*;
import project.study.jpaRepository.CertificationJpaRepository;

import java.util.Optional;

import static project.study.constant.WebConst.ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    // 인증번호는 현재시간 기준 5분 후 만료
    private static final int EXPIRE_TIME = 5;


    private final SmsRepository smsRepository;
    private final CertificationJpaRepository certificationJpaRepository;
    private final BCryptPasswordEncoder encoder;

    protected void sendSMS(RequestSms data) {
        String certificationNumber = smsRepository.createCertificationNumber();
        Message message = smsRepository.createMessage(data, certificationNumber);
//
        smsRepository.sendSms(message);

        data.setCertification(certificationNumber);
    }

    protected void regexPhone(String phone) {
        if (phone == null || phone.length() < 10) {
            throw new IllegalPhoneException();
        }
    }


    @Transactional
    protected void saveCertification(RequestSms data) {
        Certification saveCertification = Certification.builder()
                .name(data.getName())
                .phone(data.getPhone())
                .certificationNumber(data.getCertification())
                .expireDate(CustomDateTime.now().plusMinutes(EXPIRE_TIME))
                .build();

        certificationJpaRepository.save(saveCertification);
    }

    protected Certification findCertification(String certification) {
        Optional<Certification> findCertification = certificationJpaRepository.findTopByCertificationNumberOrderByCertificationId(certification);
        return findCertification.orElseThrow(() -> new NotFoundCertificationNumberException(new ResponseDto(ERROR, "인증번호가 틀렸습니다.")));
    }

    protected void validCertification(Certification certification, RequestSms data) {
        smsRepository.validCertification(certification, data);
    }
    public void validCertificationPhone(Certification certification, RequestSms data) {
        smsRepository.validCertificationPhone(certification, data);
    }

    public void validChangePassword(RequestChangePassword data) {
        if (data.getPassword() == null || data.getPasswordCheck() == null) {
            throw new SmsException(new ResponseDto(ERROR, "비밀번호를 입력해주세요."));
        }

        if (!data.getPassword().equals(data.getPasswordCheck())) {
            throw new SmsException(new ResponseDto(ERROR, "비밀번호가 일치하지 않습니다."));
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%])[A-Za-z\\d!@#$%]{8,}$"; // 비밀번호 정규식
        boolean matches = data.getPassword().matches(regex);
        if (!matches) {
            throw new SmsException(new ResponseDto(ERROR, "비밀번호는 대,소문자 특수문자 숫자를 모두 포함한 8자 이상 작성해주세요."));
        }
    }

    public FindAccount getFindAccount(RequestSms data) {
        try {
            Member member = smsRepository.findByNameAndPhone(data.getName(), data.getPhone());
            return member.findAccount();
        } catch (NotExistsMemberException e) {
            return new FindAccount(null, "회원 정보가 없습니다.");
        }
    }

    public void checkSocialMember(RequestFindPassword data) {
        Member member = smsRepository.findByNameAndPhone(data.getName(), data.getPhone());
        if (member.isSocialMember()) {
            Social social = member.getSocial();
            SocialEnum type = social.getSocialType();
            throw new NotSupportedSocialMemberException(new ResponseDto(ERROR,  "해당계정은 " + type.getKorName() + "계정입니다."));
        }
    }


    @Transactional
    public void changePassword(RequestChangePassword data) {
        Member member = smsRepository.findByNameAndPhone(data.getName(), data.getPhone());
        Basic basic = member.getBasic();
        basic.changePassword(encoder, data.getPassword());

    }

    @Transactional
    public void deleteAllByCertification(RequestSms data) {
        certificationJpaRepository.deleteAllByNameAndPhone(data.getName(), data.getPhone());
    }

}
