package me.ejpark.demoinflearnrestapi.events;

import me.ejpark.demoinflearnrestapi.accounts.Account;
import me.ejpark.demoinflearnrestapi.accounts.AccountRepository;
import me.ejpark.demoinflearnrestapi.accounts.AccountRole;
import me.ejpark.demoinflearnrestapi.accounts.AccountService;
import me.ejpark.demoinflearnrestapi.common.AppProperties;
import me.ejpark.demoinflearnrestapi.common.BaseControllerTest;
import me.ejpark.demoinflearnrestapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



public class EventControllerTests_hw extends BaseControllerTest {


    @Autowired
    EventRepository eventRepository;


    @Before
    public void Setup() {
        this.eventRepository.deleteAll();
    }

    @Test
    @TestDescription("HW: baseprice 조회")
    // 이 테스트는 도메인에 넣어도 좋다!
    // 비즈니스 로직 테스트. (도메인 객체에서 테스트)
    // 입력값이 제대로 들어오는 경우에만 test
    public void queryEventFindByBasePrice() throws Exception {

        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(get("/api/events")
                        .param("basePriceFilter", "true")
                        .param("EnrollmentDatetimeFilter", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }


    @Test
    @TestDescription("HW: CloseEnrollmentDateTime 조회")
    public void queryEventFindByCloseEnrollmentDaateTime() throws Exception {

        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(get("/api/events")
                        .param("basePriceFilter", "false")
                        .param("EnrollmentDatetimeFilter", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    @Test
    @TestDescription("HW: no Filter 조회")
    public void queryEventsNoFilter() throws Exception {

        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(get("/api/events")
                .param("basePriceFilter", "false")
                .param("EnrollmentDatetimeFilter", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    private Event generateEvent(int index) {

        LocalDateTime closeEnrollmentDateTime = LocalDateTime.of(2023, 03, 22, 22, 57).plusMonths(index);
        int basePrice = 50 + (index * 10);
        int maxPrice = 100 + (index * 10);

        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 21, 22, 57))
                .closeEnrollmentDateTime(closeEnrollmentDateTime)
                .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .endEventDateTime(LocalDateTime.of(2023, 03, 24, 22, 57))
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }
}