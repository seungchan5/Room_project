package project.study.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.study.common.CustomDateTime;
import project.study.domain.Member;
import project.study.domain.MockMember;
import project.study.dto.login.responsedto.ErrorList;
import project.study.dto.mypage.RequestChangePasswordDto;
import project.study.exceptions.RestFulException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static project.study.enums.MemberStatusEnum.*;
import static project.study.enums.SocialEnum.KAKAO;

@SpringBootTest
@DisplayName("MypageRepository")
@Transactional
class MypageRepositoryTest {

    @Autowired
    private MockMember mockMember;
    @Autowired
    private MypageRepository mypageRepository;

    @Test
    @DisplayName("소셜 회원은 비밀번호를 변경할 수 없다.")
    void validChangePassword() {
        Member socialMember = mockMember.createMember().setSocial(KAKAO).build();

        Assertions.assertThatThrownBy(() -> mypageRepository.validChangePassword(socialMember, null, new ErrorList()))
            .isInstanceOf(RestFulException.class);
    }

    @Test
    @DisplayName("일반 회원 비밀번호 변경")
    void validNickname() {
        // given
        Member basicMember = mockMember.createMember().setBasic().build();
        ErrorList errorList0 = new ErrorList();
        ErrorList errorList1 = new ErrorList();
        ErrorList errorList2 = new ErrorList();
        ErrorList errorList3 = new ErrorList();
        ErrorList errorList4 = new ErrorList();
        ErrorList errorList5 = new ErrorList();
        ErrorList errorList6 = new ErrorList();

        // 정상 흐름
        RequestChangePasswordDto data0 = new RequestChangePasswordDto("!@#QWEasd" + (basicMember.getMemberId() - 1), "!@#QWEasd" + (basicMember.getMemberId()), "!@#QWEasd" + (basicMember.getMemberId()));
        mypageRepository.validChangePassword(basicMember, data0, errorList0);
        assertThat(errorList0.hasError()).isFalse();

        // 모두가 빈 문자열 경우
        RequestChangePasswordDto data1 = new RequestChangePasswordDto("", "", "");
        mypageRepository.validChangePassword(basicMember, data1, errorList1);
        assertThat(errorList1.getErrorList().size()).isEqualTo(3);

        // 현재 비밀번호가 틀린 경우
        RequestChangePasswordDto data2 = new RequestChangePasswordDto("asdfaef", "!@#QWEasd1", "!@#QWEasd1");
        mypageRepository.validChangePassword(basicMember, data2, errorList2);
        assertThat(errorList2.hasError()).isTrue();

        // 변경할 비밀번호가 정규식에 맞지 않을경우
        RequestChangePasswordDto data3 = new RequestChangePasswordDto("!@#QWEasd" + (basicMember.getMemberId()), "!@#QWEasd", "!@#QWEasd");
        mypageRepository.validChangePassword(basicMember, data3, errorList3);
        assertThat(errorList3.hasError()).isTrue();

        RequestChangePasswordDto data4 = new RequestChangePasswordDto("!@#QWEasd" + (basicMember.getMemberId()), "!@#QWE1111111", "!@#QWE1111111");
        mypageRepository.validChangePassword(basicMember, data4, errorList4);
        assertThat(errorList4.hasError()).isTrue();

        RequestChangePasswordDto data5 = new RequestChangePasswordDto("!@#QWEasd" + (basicMember.getMemberId()), "dlekfalefk!Q ", "dlekfalefk!Q ");
        mypageRepository.validChangePassword(basicMember, data5, errorList5);
        assertThat(errorList5.hasError()).isTrue();

        // 변경비밀번호가 서로 일치하지 않을 경우
        RequestChangePasswordDto data6 = new RequestChangePasswordDto("!@#QWEasd1", "!@#QWEasdasd1", "!@#QWEasdasd2");
        mypageRepository.validChangePassword(basicMember, data6, errorList6);
        assertThat(errorList6.hasError()).isTrue();
    }

    @Test
    @DisplayName("소셜회원은 회원탈퇴 시 비밀번호가 필요하지 않다.")
    void validDeleteSocialMember() {
        // given
        Member socialMember = mockMember.createMember().setSocial(KAKAO).build();

        // then
        assertThatCode(() -> mypageRepository.validDeleteMember(socialMember, null))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("일반회원은 회원탈퇴 시 비밀번호가 일치하여야 한다.")
    void validDeleteBasicMember() {
        // given
        Member basicMember = mockMember.createMember().setBasic().build();

        // then
        assertThatThrownBy(() -> mypageRepository.validDeleteMember(basicMember, "invalidPassword"))
            .isInstanceOf(RestFulException.class);
        assertThatCode(() -> mypageRepository.validDeleteMember(basicMember, "!@#QWEasd" + (basicMember.getMemberId() - 1)))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("정지나 탈퇴회원인지 확인하는 로직")
    void validMember() {

        // given
        Member validMember1 = mockMember.createMember().setSocial(KAKAO).build();
        Member validMember2 = mockMember.createMember().setBasic().build();
        Member inValidMember3 = mockMember.createMember().setSocial(KAKAO).setExpire().build();
        Member inValidMember4 = mockMember.createMember().setSocial(KAKAO).setFreeze(CustomDateTime.now().plusDays(1)).build();
        Member inValidMember5 = mockMember.createMember().setBasic().setFreeze(CustomDateTime.now().plusDays(1)).build();
        Member inValidMember6 = mockMember.createMember().setBasic().setExpire().build();

        // 정상로직
        assertThatCode(() -> mypageRepository.validMember(validMember1, new ErrorList()))
            .doesNotThrowAnyException();
        assertThatCode(() -> mypageRepository.validMember(validMember2, new ErrorList()))
            .doesNotThrowAnyException();

        // 예외로직
        assertThatThrownBy(() -> mypageRepository.validMember(inValidMember3, new ErrorList()))
            .isInstanceOf(RestFulException.class);
        assertThatThrownBy(() -> mypageRepository.validMember(inValidMember4, new ErrorList()))
            .isInstanceOf(RestFulException.class);
        assertThatThrownBy(() -> mypageRepository.validMember(inValidMember5, new ErrorList()))
            .isInstanceOf(RestFulException.class);
        assertThatThrownBy(() -> mypageRepository.validMember(inValidMember6, new ErrorList()))
            .isInstanceOf(RestFulException.class);
    }
}