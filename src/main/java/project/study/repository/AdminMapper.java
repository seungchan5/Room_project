package project.study.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.study.authority.admin.dto.*;
import project.study.enums.NotifyType;
import java.util.List;

@Mapper
public interface AdminMapper {

    List<SearchMemberDto> searchMemberList(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("word") String word, @Param("freezeOnly") String freezeOnly);
    int getTotalMemberCnt(@Param("word") String word, @Param("freezeOnly") String freezeOnly);

    List<SearchExpireMemberDto> searchExpireMemberList(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("word") String word);
    int getTotalExpireMemberCnt(String word);

    List<SearchRoomDto> searchRoomList(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("word") String word);
    int getTotalRoomCnt(String word);

    void joinRoomDelete(RequestDeleteRoomDto dto);
    void insertRoomDelete(RequestDeleteRoomDto dto);

    List<SearchNotifyDto> searchNotifyList(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("word") String word, @Param("containComplete") String containComplete);
    int getTotalNotifyCnt(@Param("word") String word, @Param("containComplete") String containComplete);

    SearchNotifyReadMoreDtoBatis notifyReedMore(Long notifyId);
    List<SearchNotifyImageDtoBatis> notifyImage(Long notifyId);
    void notifyStatusChange(RequestNotifyStatusChangeDto dto);
    SearchNotifyMemberInfoDto notifyMemberInfo(Long notifyId);
    String notifyMemberProfile(String account);

    void notifyMemberFreeze(Long memberId);
    void notifyMemberBan(Long memberId);
    Long searchFreezeId(RequestLiftBanDto dto);
    void banInsert(RequestNotifyMemberFreezeDto dto);
    String freezeSelect(Long memberId);
    void banFreeze(RequestNotifyMemberFreezeDto dto);
    void newFreeze(@Param("freezePeriod") int freezePeriod, @Param("memberId") Long memberId, @Param("freezeReason") NotifyType freezeReason);
    void plusFreeze(@Param("newFreezePeriod") String newFreezePeriod, @Param("memberId") Long memberId, @Param("freezeReason") NotifyType freezeReason);

    List<SearchBanDto> searchBanList(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("word") String word);
    int getTotalBanCnt(@Param("word") String word);
    Long searchBanOne(Long memberId);
    void deleteBan(RequestLiftBanDto dto);
    void deleteFreeze(Long freezeId);
    void banMemberStatusChangeNormal(RequestLiftBanDto dto);
    void banMemberStatusChangeFreeze(RequestLiftBanDto dto);
}
