package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Admin;
import java.util.Optional;

public interface AdminJpaRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAccount(String account);
}
