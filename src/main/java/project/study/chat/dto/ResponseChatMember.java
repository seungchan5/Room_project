package project.study.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ResponseChatMember {

    private Collection<String> currentMemberList;



}
