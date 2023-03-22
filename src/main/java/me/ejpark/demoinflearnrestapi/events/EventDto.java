package me.ejpark.demoinflearnrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

// 입력값을 받는 Dto
// 중복이 생긴다는 단점
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    // 이만큼만 받을 수 있다고 명시.
    // 이름이 안온다. 설명이 없다. 날짜도 없다... 들어와야 하는 정보가 안 온 경우!
    // 검증 수행
    @NotEmpty
    private String name;
    @NotEmpty

    private String description;
    @NotNull
    private LocalDateTime beginEnrollmentDateTime; // springboot 2.1부터는 jpa 3.2 지원. localDateTime 기본 매핑 지원.
    @NotNull

    private LocalDateTime closeEnrollmentDateTime;
    @NotNull
    private LocalDateTime beginEventDateTime;
    @NotNull
    private LocalDateTime endEventDateTime;

    private String location; // (optional) 이게 없으면 온라인 모임
    @Min(0)
    private int basePrice; // optional
    @Min(0)
    private int maxPrice; // optional
    @Min(0)
    private int limitOfEnrollment;
}
