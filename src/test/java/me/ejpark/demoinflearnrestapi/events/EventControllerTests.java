package me.ejpark.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ejpark.demoinflearnrestapi.common.RestDocConfiguration;
import me.ejpark.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
//@WebMvcTest // 슬라이스 테스트라 웹용 빈만 등록.
@SpringBootTest // webenvironment param = 기본값: mock. 계속 mockmvc 테스트 사용 가능.
@AutoConfigureMockMvc // SpringBootTest랑 같이 쓰려면
// mocking할 게 많으면 테스트코드 짜기 어렵고 관리도 어려움. springbootTest 사용.
@AutoConfigureRestDocs // rest docs
@Import(RestDocConfiguration.class) // bean 설정 import해서 사용
@ActiveProfiles("test") // test 밑의 application-test.properties도 사용! 그러므로 test는 계속 h2를 사용하게 된다.
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // 가짜 디스패처 서블릿 만들어서 응답 보내기 테스트 가능. slicing test
    // 웹 서버 띄우지 않음 -> 빠름. 단위 테스트보다는 느림


    // content-type: json 변환
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        // 요청 만들기
        // 이벤트 빌더 사용
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 21, 22, 57))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 22, 22, 57))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .endEventDateTime(LocalDateTime.of(2023, 03, 24, 22, 57))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build()
                ;

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())

                // 존재하는지, 헤더에 특정 type 이 들어왔는지
                .andExpect(header().exists(HttpHeaders.LOCATION)) // TYPE-SAFE (string 대신 type으로)
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))


                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true)) // location이 있으므로 true라고 나와야 한다
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name())) // 생성된 직후에는 draft 상태여야 함

                .andDo(document("create-event", // 타겟 디렉토리 (target 폴더 밑에 generated-snipptes/create-event 생성)
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an event"),
                                linkWithRel("profile").description("link to update an event")

                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")

                        ),
                        requestFields(
                                // 요청으로 받는 것들 (curl-request.adoc 켜 놓고 하기)
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")

                        ),

                        // response
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"), // 새로 생성된 이벤트를 조회할 수 있는 URL
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type") // HAL-json
                        ),
                        responseFields(

                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base Price of new event"),
                                fieldWithPath("maxPrice").description("max Price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit Of Enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),

                                // relaxed prefix 사용하거나 이렇게 다 명시해주거나... (relaxed보다는 전부 다 문서화하는 것을 권장. API 변경시 바로 바뀌기 때문)
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")

                        )
                    )

                );


    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")

    // 입력값이 이상하게 들어오는 경우의 test
    public void createEvent_Bad_Request() throws Exception {

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
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build()
                ;

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())


        ;

    }

    // 들어와야 할 값이 안 들어왔을 때
    @Test
    @TestDescription("입력값이 비어 있는 경우에 에러가 발생하는 테스트")

    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build(); // 빈값으로 보내기

        this.mockMvc.perform(post("/api/events") // 이거 하려면 늘 메서드에 exception 있어야 함
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")

    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API development with Spring")

                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 24, 22, 57))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 20, 22, 57))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .endEventDateTime(LocalDateTime.of(2023, 03, 22, 22, 57))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events") // 이거 하려면 늘 메서드에 exception 있어야 함
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())


        ;
    }
}
