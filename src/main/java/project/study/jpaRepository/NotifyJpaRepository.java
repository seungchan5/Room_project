package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Member;
import project.study.domain.Notify;
import project.study.enums.NotifyStatus;

public interface NotifyJpaRepository extends JpaRepository<Notify, Long> {

    boolean existsByCriminalAndNotifyStatus(Member criminal, NotifyStatus notifyStatus);

    void deleteAllByCriminal(Member member);
}
