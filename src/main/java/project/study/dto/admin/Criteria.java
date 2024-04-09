package project.study.dto.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class Criteria {

    private final int amount = 10;

    public int getStartNum(int pageNumber) {
        return (pageNumber-1) * amount;
    }

    public int getEndNum(int pageNumber) {
        return (pageNumber-1) * amount + amount;
    }

    public Pageable getPageable(int pageNumber) {
        return PageRequest.of(pageNumber-1, amount);
    }
}
