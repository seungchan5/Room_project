package project.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.study.authority.member.dto.RequestNotifyDto;
import project.study.chat.ChatRepository;
import project.study.common.CustomDateTime;
import project.study.constant.WebConst;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.domain.Member;
import project.study.domain.Notify;
import project.study.domain.Room;
import project.study.dto.abstractentity.ResponseDto;
import project.study.enums.NotifyStatus;
import project.study.exceptions.RestFulException;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.NotifyJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotifyService {

    private final ChatRepository chatRepository;
    private final NotifyJpaRepository notifyJpaRepository;
    private final FileUpload fileUpload;


    public Notify saveNotify(Member reporter, Room room, RequestNotifyDto data) {
        Optional<Member> findCriminal = chatRepository.findByMemberNickname(room, data.getNickname());

        Member criminal = findCriminal.orElseThrow(() -> new RestFulException(new ResponseDto(WebConst.ERROR, "존재하지 않는 회원입니다.")));

        Notify saveNotify = Notify.builder()
            .reporter(reporter)
            .criminal(criminal)
            .notifyDate(CustomDateTime.now())
            .room(room)
            .notifyContent(data.getNotifyContent())
            .notifyStatus(NotifyStatus.처리중)
            .notifyReason(data.getNotifyType())
            .build();

        notifyJpaRepository.save(saveNotify);
        return saveNotify;
    }

    public void saveNotifyImage(Notify saveNotify, RequestNotifyDto data) {
        List<MultipartFile> images = data.getImages();
        if (images == null) return;

        for (MultipartFile image : images) {
            fileUpload.saveFile(image, FileUploadType.NOTIFY_IMAGE, saveNotify);
        }
    }
}
