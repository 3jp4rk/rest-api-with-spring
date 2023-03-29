package me.ejpark.demoinflearnrestapi.events;

import lombok.*;
import me.ejpark.demoinflearnrestapi.events.EventStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor @Getter @Setter
@EqualsAndHashCode(of = "id")
public class Event {

    // Entity annotation 붙이는 순간 id 어노테이션 붙여 줘야 함
    @Id @GeneratedValue
    private Integer id;
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
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING) // ordinal(기본값)은 enum 순서대로 0 1 2 이렇게 부여함. string으로 저장 권장.
    private EventStatus eventStatus = EventStatus.DRAFT; // 초기값 draft로 설정

    public void update() {
        // Update free
        // 삼항 연산자 이용해서 리팩토링해도 됨
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }

        // Update offline
        if (this.location == null || this.location.isBlank()) { // java 11부터 새로 생긴 isBlaank 메서드. 그전에는 trim해서 isEmpty() 확인.
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
