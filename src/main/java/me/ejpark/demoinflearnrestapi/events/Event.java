package me.ejpark.demoinflearnrestapi.events;

import lombok.*;
import me.ejpark.demoinflearnrestapi.events.EventStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor @Getter @Setter
@EqualsAndHashCode(of = "id") // 객체 간 연관관계 있을 때. 해쉬코드 구현할 때 모든 필드를 사용하는데, 엔티티간 연관관계가 생기면 연관관계가 상호 참조 -> equals해시코드 구현한 코드 안에서 스택오버플로우 발생 가능.
//@EqualsAndHashCode(of = {"id", "account"}) // 여러 키 해도 되는데 연관된 건 안 됨

// Entity에는 Data도 쓰면 안 됨! 상호참조 스택오버플로우 발생.
// 메타 어노테이션 (custom) @MyEntity 이런식으로... 만들고 등록 가능.. 스프링 어노테이션은 되는데 lombok annotation은 안됨
@Entity
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

// 등록 가능한 인원수 10명 -> 경매제처럼 최저금액 사람은 더 높은 금액을 낸 사람이 큐에 들어가는 식으로.. (무제한 경매)
// Event 생성 API의 결과값은 status는 기본이 draft.
// profile: doc link
// self: 이벤트 생성 API일 경우, 생성한 API 정보를 확인할 수 있는 값들