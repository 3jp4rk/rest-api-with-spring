package me.ejpark.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
//@WebMvcTest // 슬라이스 테스트라 웹용 빈만 등록.
@SpringBootTest // webenvironment param = 기본값: mock. 계속 mockmvc 테스트 사용 가능.
@AutoConfigureMockMvc // SpringBootTest랑 같이 쓰려면
// mocking할 게 많으면 테스트코드 짜기 어렵고 관리도 어려움. springbootTest 사용.
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // 가짜 디스패처 서블릿 만들어서 응답 보내기 테스트 가능. slicing test
    // 웹 서버 띄우지 않음 -> 빠름. 단위 테스트보다는 느림


    // content-type: json 변환
    @Autowired
    ObjectMapper objectMapper;

    // springbootTest 사용시 repository 선언되어 있다면 삭제해야 함 mocking 사용안한다
    // 슬라이스 테스트는 web용 bean만 등록 respository는 bean으로 등록 안 함. -> repository bean .못 찾아서 에러남
    // 레포지토리 모킹해야 됨 = mock으로 만들어 달라

    @Test
    public void createEvent() throws Exception {

        // 요청 만들기
        // 이벤트 빌더 사용
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 21, 22, 57))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 22, 22, 57))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .endEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")

                // 입력값 제한하기
                // id도 DB에 들어갈 때 값이 자동 생성되어야 하고, 값이 다 있으면 free도 true가 아니고... location이 있으니까 offline은 true가 맞는데 이런 식으로 집어넣을 수 있게 됨. 이러면 안 도미.
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED) // 이렇게 주더라도 안 됨 (dto에서 입력하기로 한 값 이외에는 무시)
                .build()
                ;
        // mockbean return값 null이라서 발생하는 nullpointer exception
//        event.setId(10);
        // 스터빙
        // eventController.java 에서는 mocking을 안하는데 여기서는 mocking하니까 null이 들어감
        // slicing test 해제

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        // 요청 추가 (json 변환 후 본문 객체에 넣어 줌)
                        .content(objectMapper.writeValueAsString(event))
                )// accept header를 통해 원하는 응답 알려줌. accept 헤더 지정하는 게 HTTP 응답에 더 맞는 듯.
                .andDo(print()) // 무슨 요청, 무슨 응답인지 확인 가능. MockHttpServletResponse: 출력 확인
                .andExpect(status().isCreated()) // isCreated() 대신 201
                .andExpect(jsonPath("id").exists())

                // 존재하는지, 헤더에 특정 type 이 들어왔는지
                .andExpect(header().exists(HttpHeaders.LOCATION)) // TYPE-SAFE (string 대신 type으로)
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))

                // 입력값 제한
                // 이런 값이 들어오면 안된다
                // DTO 사용. jacksonjson의 annotation 사용해도 되긴 함. 그런데 도메인 클래스에 어노테이션이 너무 많아지면 좀 그러니까...
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT.name()))) // 생성된 직후에는 draft 상태여야 함




        // 최소 데이터 3개는 가지고 만들어야 함... 구현 전에 Test부터 만들어야 (TDD) 삼각정량법?? 측량법?


        ; // 세미콜론 여기 두기


    }



}
