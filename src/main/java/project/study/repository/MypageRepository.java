package project.study.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.domain.Basic;
import project.study.domain.Member;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.abstractentity.ResponseObject;
import project.study.dto.login.responsedto.Error;
import project.study.dto.login.responsedto.ErrorList;
import project.study.dto.mypage.RequestChangePasswordDto;
import project.study.dto.mypage.RequestEditInfoDto;
import project.study.exceptions.RestFulException;
import project.study.service.SignupService;

import static project.study.constant.WebConst.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MypageRepository {

    private final SignupService signupService;
    private final BCryptPasswordEncoder encoder;

    public void validChangePassword(Member member, RequestChangePasswordDto data, ErrorList errorList) {
        if (member.isSocialMember()) {
            throw new RestFulException(new ResponseDto(ERROR, "소셜회원은 비밀번호를 변경할 수 없습니다."));
        }
        String nowPassword = data.getNowPassword();
        String changePassword = data.getChangePassword();
        String checkPassword = data.getCheckPassword();

        if (nowPassword == null || nowPassword.isEmpty()) {
            errorList.addError(new Error("bfpw", "현재 비밀번호를 입력해주세요."));
        }
        if (changePassword == null || changePassword.isEmpty()) {
            errorList.addError(new Error("cpw", "변경할 비밀번호를 입력해주세요."));
        }
        if (checkPassword == null || checkPassword.isEmpty()) {
            errorList.addError(new Error("cpwc", "변경할 비밀번호는 한번 더 입력해주세요."));
        }
        if (errorList.hasError()) return;

        Basic basic = member.getBasic();
        boolean matches = basic.isValidPassword(encoder, nowPassword);
        if (!matches) {
            errorList.addError(new Error("bfpw", "비밀번호가 일치하지 않습니다."));
            return;
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%])[A-Za-z\\d!@#$%]{8,}$"; // 비밀번호 정규식
        if (!changePassword.matches(regex)) {
            errorList.addError(new Error("cpw", "8자 이상 대,소문자, 숫자, 특수문자(!@#$%)를 포함해야 합니다."));
            return;
        }

        if (!changePassword.equals(checkPassword)) {
            errorList.addError(new Error("cpw", "변경할 비밀번호가 일치하지 않습니다."));
        }

    }

    @Transactional
    public void changePassword(Member member, RequestChangePasswordDto data) {
        Basic basic = member.getBasic();
        basic.changePassword(encoder, data.getChangePassword());
    }

    public void validNickname(Member member, RequestEditInfoDto data) {
        String memberNickname = member.getMemberNickname();
        if (memberNickname.equals(data.getNickname())) return;
        signupService.distinctNickname(data.getNickname());

    }

    public void validDeleteMember(Member member, String password) {
        if (member.isSocialMember()) return;

        Basic basic = member.getBasic();
        boolean isValidPassword = basic.isValidPassword(encoder, password);
        if (!isValidPassword) {
            throw new RestFulException(new ResponseDto(ERROR, "비밀번호가 일치하지 않습니다."));
        }
    }

    public void validMember(Member member, ErrorList errorList) {
        if (member.isFreezeMember()) {
            errorList.addError(new Error(ERROR, "정지된 회원입니다."));
        } else if (member.isExpireMember()) {
            errorList.addError(new Error(ERROR, "이미 탈퇴한 회원입니다."));
        }
        if (errorList.hasError()) {
            throw new RestFulException(new ResponseObject<>(ERROR, "에러", errorList.getErrorList()));
        }
    }
}
