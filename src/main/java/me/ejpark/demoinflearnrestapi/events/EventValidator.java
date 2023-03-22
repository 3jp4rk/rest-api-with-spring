package me.ejpark.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

// validator에 대한 unittest도 따로 만들 수 있다
@Component // 빈 등록 -> controler에 주입
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
               // 1번만 만족시 가능하기는 한데
                // 2번부터는 비즈니스 로직 위배
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong."); // 마우스 호버링하면 parameter 팝업됨
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong."); // 마우스 호버링하면 parameter 팝업됨
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        // 날짜 검증
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "endEventDateTime is Wrong Value.");
        }

        // TODO beginEventDateTime
        // TODO closeEnrollmentDatetTime
    }
}
