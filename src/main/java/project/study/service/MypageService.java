package project.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.constant.WebConst;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.domain.Member;
import project.study.dto.abstractentity.ResponseObject;
import project.study.dto.login.responsedto.ErrorList;
import project.study.dto.mypage.RequestChangePasswordDto;
import project.study.dto.mypage.RequestDeleteMemberDto;
import project.study.dto.mypage.RequestEditInfoDto;
import project.study.exceptions.RestFulException;
import project.study.repository.MypageRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MypageService {

    private final MypageRepository mypageRepository;
    private final FileUpload fileUpload;


    public void editInfo(Member member, RequestEditInfoDto data) {
        mypageRepository.validNickname(member, data);
        fileUpload.editFile(data.getProfile(), FileUploadType.MEMBER_PROFILE, member);
        member.updateInfo(data);
    }

    public void changePassword(Member member, RequestChangePasswordDto data) {
        ErrorList errorList = new ErrorList();
        mypageRepository.validMember(member, errorList);
        mypageRepository.validChangePassword(member, data, errorList);
        if (errorList.hasError()) {
            throw new RestFulException(new ResponseObject<>(WebConst.ERROR, "에러", errorList.getErrorList()));
        }
        mypageRepository.changePassword(member, data);

    }

    public void deleteMember(Member member, RequestDeleteMemberDto data) {
        mypageRepository.validDeleteMember(member, data.getPassword());
        member.changeStatusToExpire();
    }
}
