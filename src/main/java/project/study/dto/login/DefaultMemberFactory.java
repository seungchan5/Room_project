package project.study.dto.login;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.study.controller.image.FileUpload;
import project.study.dto.login.validator.DefaultMemberValidator;
import project.study.dto.login.validator.MemberValidator;
import project.study.jpaRepository.BasicJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.ProfileJpaRepository;
import project.study.repository.FreezeRepository;

@RequiredArgsConstructor
public class DefaultMemberFactory implements MemberFactory{

    private final BasicJpaRepository basicJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final FreezeRepository freezeRepository;
    private final FileUpload fileUpload;
    private final BCryptPasswordEncoder encoder;

    @Override
    public MemberInterface createMember() {
        return new DefaultMember(basicJpaRepository, memberJpaRepository, encoder);
    }

    @Override
    public MemberValidator validator() {
        return new DefaultMemberValidator(freezeRepository, basicJpaRepository, memberJpaRepository);
    }

    @Override
    public FileUpload getFileUpload() {
        return fileUpload;
    }


}
