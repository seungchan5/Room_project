package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.admin.dto.*;
import project.study.common.CustomDateTime;
import project.study.domain.*;
import project.study.dto.admin.Criteria;
import project.study.exceptions.admin.AlreadyBanMemberException;
import project.study.jpaRepository.AdminJpaRepository;
import project.study.repository.AdminMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminJpaRepository adminJpaRepository;
    private final AdminMapper adminMapper;
    private final Criteria criteria;

    public Page<SearchMemberDto> searchMemberList(int pageNumber, String word, String freezeOnly){
        List<SearchMemberDto> data = adminMapper.searchMemberList(criteria.getStartNum(pageNumber), criteria.getEndNum(pageNumber), word, freezeOnly);
        return new PageImpl<>(data, criteria.getPageable(pageNumber), adminMapper.getTotalMemberCnt(word, freezeOnly));
    }

    public Page<SearchExpireMemberDto> searchExpireMemberList(int pageNumber, String word){
        List<SearchExpireMemberDto> data = adminMapper.searchExpireMemberList(criteria.getStartNum(pageNumber), criteria.getEndNum(pageNumber), word);
        return new PageImpl<>(data, criteria.getPageable(pageNumber), adminMapper.getTotalExpireMemberCnt(word));
    }

    public Page<SearchRoomDto> searchRoomList(int pageNumber, String word){
        List<SearchRoomDto> data = adminMapper.searchRoomList(criteria.getStartNum(pageNumber), criteria.getEndNum(pageNumber), word);
        return new PageImpl<>(data, criteria.getPageable(pageNumber), adminMapper.getTotalRoomCnt(word));
    }

    @Transactional
    public void roomDelete(RequestDeleteRoomDto dto){
        adminMapper.joinRoomDelete(dto);
        adminMapper.insertRoomDelete(dto);
    }

    public Page<SearchNotifyDto> searchNotifyList(int pageNumber, String word, String containComplete){
        List<SearchNotifyDto> data = adminMapper.searchNotifyList(criteria.getStartNum(pageNumber), criteria.getEndNum(pageNumber), word, containComplete);
        return new PageImpl<>(data, criteria.getPageable(pageNumber), adminMapper.getTotalNotifyCnt(word, containComplete));
    }

    public SearchNotifyReadMoreDtoBatis notifyReedMoreBatis(Long notifyId){
        SearchNotifyReadMoreDtoBatis searchNotifyReadMoreDtoBatis = adminMapper.notifyReedMore(notifyId);
        List<SearchNotifyImageDtoBatis> searchNotifyImageDtoBatis = adminMapper.notifyImage(notifyId);

        searchNotifyReadMoreDtoBatis.setNotifyImages(searchNotifyImageDtoBatis);
        return searchNotifyReadMoreDtoBatis;
    }

    @Transactional
    public void notifyComplete(RequestNotifyStatusChangeDto dto){
        adminMapper.notifyStatusChange(dto);
    }

    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(Long notifyId, String account){
        SearchNotifyMemberInfoDto searchNotifyMemberInfoDto = adminMapper.notifyMemberInfo(notifyId);
        String s = adminMapper.notifyMemberProfile(account);
        searchNotifyMemberInfoDto.setMemberProfile(s);

        return searchNotifyMemberInfoDto;
    }

    @Transactional
    public void notifyFreeze(RequestNotifyMemberFreezeDto dto, HttpServletResponse response){
        int freezePeriod = dto.getFreezePeriod().getBanEnum();
        Long countBanMember = adminMapper.searchBanOne(dto.getMemberId());
        if(countBanMember!=null){
            throw new AlreadyBanMemberException(response, "이미 영구 정지된 회원입니다");
        }

        if(freezePeriod == 9999){
            adminMapper.notifyMemberBan(dto.getMemberId());
            adminMapper.banFreeze(dto);
            adminMapper.banInsert(dto);
        } else {

            String selectString = adminMapper.freezeSelect(dto.getMemberId());
            if (selectString == null) {
                adminMapper.notifyMemberFreeze(dto.getMemberId());
                adminMapper.newFreeze(freezePeriod, dto.getMemberId(), dto.getFreezeReason());

            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(selectString, formatter);
                LocalDateTime localDateTime = dateTime.plusDays(freezePeriod);
                String newFreezePeriod = localDateTime.format(formatter);
                adminMapper.plusFreeze(newFreezePeriod, dto.getMemberId(), dto.getFreezeReason());
                }
            }

    }

    public Page<SearchBanDto> searchBanList(int pageNumber, String word){
        List<SearchBanDto> data = adminMapper.searchBanList(criteria.getStartNum(pageNumber), criteria.getEndNum(pageNumber), word);
        return new PageImpl<>(data, criteria.getPageable(pageNumber), adminMapper.getTotalBanCnt(word));
    }

    @Transactional
    public void liftTheBan(RequestLiftBanDto dto){
        adminMapper.deleteBan(dto);
        Long freezeId = adminMapper.searchFreezeId(dto);
        adminMapper.deleteFreeze(freezeId);
        String freezeDate = adminMapper.freezeSelect(dto.getMemberId());
        if(freezeDate == null){
            adminMapper.banMemberStatusChangeNormal(dto);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime freezeEndDate = LocalDateTime.parse(freezeDate, formatter);
            LocalDateTime now = CustomDateTime.now();
            if(now.isAfter(freezeEndDate)){
                adminMapper.banMemberStatusChangeNormal(dto);
            } else {
                adminMapper.banMemberStatusChangeFreeze(dto);
            }
        }
    }

    @Transactional
    public Optional<Admin> adminLogin(String account, String password){
        Optional<Admin> byAccount = adminJpaRepository.findByAccount(account);

        if(byAccount.isPresent() && byAccount.get().isMatchesPassword(password)){
            return adminJpaRepository.findByAccount(account);
        }
        return Optional.empty();
    }

    public Optional<Admin> findById(Long id){
        if (id == null) return Optional.empty();
        return adminJpaRepository.findById(id);
    }

}
