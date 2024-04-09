package project.study.controller.api.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.study.customAnnotation.CallType;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Certification;
import project.study.domain.Member;
import project.study.dto.abstractentity.ResponseDto;

@RestController
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/sms/send")
    public ResponseEntity<ResponseDto> sendSMS(@RequestBody RequestSms data) {
        return ResponseEntity.badRequest().body(new ResponseDto("error", "배포용 페이지는 이 기능을 사용할 수 없습니다."));
//
//        smsService.regexPhone(data.getPhone());
//
//        smsService.sendSMS(data);
//
//        smsService.saveCertification(data);
//
//        return ResponseEntity.ok(new ResponseDto("인증번호를 발송했습니다."));
    }
//
//    @PostMapping("/sms/account/confirm")
//    public ResponseEntity<ResponseDto> accountConfirm(@RequestBody RequestSms data) {
//
//        Certification certification = smsService.findCertification(data.getCertification());
//
//        smsService.validCertification(certification, data);
//
//        FindAccount findAccount = smsService.getFindAccount(data);
//
//        smsService.deleteAllByCertification(data);
//
//        return ResponseEntity.ok(new ResponseAccountDto("인증이 완료되었습니다.", findAccount));
//    }
//
//    @PostMapping("/sms/password/confirm")
//    public ResponseEntity<ResponseDto> passwordConfirm(@RequestBody RequestFindPassword data) {
//
//        Certification certification = smsService.findCertification(data.getCertification());
//
//        smsService.validCertification(certification, data);
//
//        smsService.checkSocialMember(data);
//
//        return ResponseEntity.ok(new ResponseDto("인증이 완료되었습니다."));
//    }
//
//    @PostMapping("/changePassword")
//    public ResponseEntity<ResponseDto> changePassword(@RequestBody RequestChangePassword data) {
//
//        Certification certification = smsService.findCertification(data.getCertification());
//
//        smsService.validCertification(certification, data);
//
//        smsService.checkSocialMember(data);
//
//        smsService.validChangePassword(data);
//
//        smsService.changePassword(data);
//
//        return ResponseEntity.ok(new ResponseDto("비밀번호 변경 완료"));
//    }
//
//    @PostMapping("/changePhone")
//    public ResponseEntity<ResponseDto> changePhone(@SessionLogin(required = true, type = CallType.REST_CONTROLLER) Member member,
//                                                   @RequestBody RequestSms data) {
//
//        Certification certification = smsService.findCertification(data.getCertification());
//
//        smsService.validCertificationPhone(certification, data);
//
//        member.changePhone(certification.getPhone());
//
//        return ResponseEntity.ok(new ResponseDto("휴대폰 변경 완료"));
//    }

}
