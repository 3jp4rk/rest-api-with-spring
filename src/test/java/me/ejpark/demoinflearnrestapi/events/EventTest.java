package me.ejpark.demoinflearnrestapi.events;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test // 메서드에 붙이는 것
    public void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring API test")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();

    } // 컴파일 시점에 추가적인 코드가 생성됨 (@build 어노테이션)
    // 테스트 코드 실행하면 이미 다 생겨 있음

    @Test
    public void javaBean() {
//        Event event = new Event();
        // 리팩토링 전
//        String name = "Event";
//        event.setName(name); // getter setter // code 구현하지 않고 lombok annotation만 붙였는데도 이걸 사용할 수가 있다
//        event.setDescription("Spring");
//        assertThat(event.getName()).isEqualTo("Event");
//        assertThat(event.getDescription()).isEqualTo("Spring");

        // 리팩토링 후 (assertThat 자동완성이 너무 버벅여서) 단축키: 블록 지정 후 alt+ctrl+v
        // 테스트 실행 단축키: ctrl shift R?

        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);

    }

    // 비즈니스 로직 테스트
    @Test
    public void testFree() {

        // Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isTrue();


        // Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse(); // 무료 아님


        // Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse(); // 무료 아님
    }

    // 온라인 오프라인 여부 테스트 (단위 테스트)
    @Test
    public void testoffline() {
        // Given
        Event event = Event.builder()
                .location("강남역 네이버 D2 스타트업 팩토리")
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue(); // 장소 있으므로 오프라인


        // Given
        event = Event.builder()
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse(); // 장소 없으므로 오프라인 아님
    }

}