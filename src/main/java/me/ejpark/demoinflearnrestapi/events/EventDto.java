package me.ejpark.demoinflearnrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 입력값을 받는 Dto
// 중복이 생긴다는 단점
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    // 이만큼만 받을 수 있다고 명시.
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime; // springboot 2.1부터는 jpa 3.2 지원. localDateTime 기본 매핑 지원.
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;

    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // optional
    private int maxPrice; // optional
    private int limitOfEnrollment;
}
