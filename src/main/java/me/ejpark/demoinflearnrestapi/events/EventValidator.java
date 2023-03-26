package me.ejpark.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
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
            errors.reject("wrongPrices", "Values for prices are wrong.");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        // 날짜 검증
        // 1. 끝나는 날짜가 시작하는 날짜보다 이르거나 2. 시작하는 날짜가 끝나는 날짜보다 늦거나
        // 3. 등록 시작일, 등록 마감일, 이벤트 시작일, 이벤트 마감일

        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "endEventDateTime is Wrong Value.");
        }

        // TODO beginEventDateTime
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        // 날짜 검증
        if (beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
                beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("beginEventDateTime", "beginEventDateTime is Wrong Value.");
        }

        // TODO closeEnrollmentDatetTime
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        // 날짜 검증
        if (closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
                closeEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime()) ||
                closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", "closeEnrollmentDateTime is Wrong Value.");
        }

    }
}
