package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Certification;

import java.util.Optional;


public interface CertificationJpaRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findTopByCertificationNumberOrderByCertificationId(String phone);

    void deleteAllByNameAndPhone(String name, String phone);
}
