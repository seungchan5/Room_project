package project.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.jpaRepository.ChatJpaRepository;
import project.study.controller.image.FileUploadRepository;
import project.study.controller.image.FileUploadType;
import project.study.domain.*;
import project.study.enums.NotifyStatus;
import project.study.jpaRepository.*;
import project.study.service.JoinRoomService;


@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleRepository {

    private final FileUploadRepository fileUploadRepository;

    private final MemberJpaRepository memberJpaRepository;
    private final BasicJpaRepository basicJpaRepository;
    private final SocialTokenJpaRepository socialTokenJpaRepository;
    private final SocialJpaRepository socialJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final FreezeJpaRepository freezeJpaRepository;

    private final RoomNoticeJpaRepository roomNoticeJpaRepository;
    private final RoomImageJpaRepository roomImageJpaRepository;
    private final RoomPasswordJpaRepository roomPasswordJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final RoomDeleteJpaRepository roomDeleteJpaRepository;
    private final ChatJpaRepository chatJpaRepository;
    private final RoomJpaRepository roomJpaRepository;
    private final NotifyJpaRepository notifyJpaRepository;
    private final JoinRoomService joinRoomService;

    public void deleteJoinRoom(Member member) {
        member.getJoinRoomList().iterator().forEachRemaining(joinRoomService::exitRoom);
    }

    public void deleteAccount(Member member) {
        if (member.isBasicMember()) {
            basicJpaRepository.deleteByMember(member);
        }
        if (member.isSocialMember()) {
            socialTokenJpaRepository.deleteBySocial(member.getSocial());
            socialJpaRepository.deleteByMember(member);
        }
    }

    public void deleteFreeze(Member member) {
        freezeJpaRepository.deleteAllByMember(member);
    }

    public void deleteMember(Member member) {

        fileUploadRepository.deleteImage(FileUploadType.MEMBER_PROFILE, member);
        profileJpaRepository.deleteByMember(member);
        memberJpaRepository.delete(member);
    }

    public boolean hasNotifyAsCriminal(Member member) {
        return notifyJpaRepository.existsByCriminalAndNotifyStatus(member, NotifyStatus.처리중);
    }

    public void deleteRoom(RoomDelete roomDelete) {
        Room room = roomDelete.getRoom();

        if (room.hasNotice()) {
            roomNoticeJpaRepository.deleteAllByRoom(room);
        }
        if (room.hasRoomPassword()) {
            roomPasswordJpaRepository.deleteByRoom(room);
        }
        fileUploadRepository.deleteImage(FileUploadType.ROOM_PROFILE, room);
        roomImageJpaRepository.deleteByRoom(room);

        tagJpaRepository.deleteAllByRoom(room);
        roomDeleteJpaRepository.delete(roomDelete);

        chatJpaRepository.deleteAllByRoom(room);

        roomJpaRepository.delete(room);
    }

    public void deleteNotify(Member member) {
        notifyJpaRepository.deleteAllByCriminal(member);
    }
}
