package me.ejpark.demoinflearnrestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class) // runner 설정
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
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {

        // 테스트 코드가 거의 똑같고 파라미터만 변경되는 형식이라면?
        // 중복 제거 -> JunitParams 사용!
        // junit은 원래 메서드 파라미터를 사용할 수 없는데, JunitParams는 메서드 파라미터를 사용할 수 있게 해 준다.
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    // type safe version
    private Object[] parametersForTestFree() { // ParametersFor{테스트 메서드 이름} 이 컨벤션임.
        // 이렇게 명명할 경우 parameters에 method 인자 생략할 수 있다. 알아서 찾음.
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    // 온라인 오프라인 여부 테스트 (단위 테스트)
    @Test
    @Parameters // 이거 없으면 Junit으로 돌기 떄문에 꼭 붙여저야 함
    public void testOffline(String location, boolean isOffline) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline); // 장소 있으므로 오프라인
    }

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"강남", true},
                new Object[] {null, false},
                new Object[] {"       ", false}
        };
    }

}