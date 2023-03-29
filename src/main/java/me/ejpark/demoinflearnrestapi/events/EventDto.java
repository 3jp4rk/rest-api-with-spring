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
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

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
