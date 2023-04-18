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



public class EventControllerTests extends BaseControllerTest {

    // 어노테이션 중복 -> 상속 사용!!! 

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService; // oAuth 인증

    @Autowired
    AccountRepository accountRepository;

    @Autowired // Type-safe -> AppProperties 사용
    AppProperties appProperties;


    @Before // 중복 id 삭제하기 (테스트하기 전ㄴ에 매번 DB 비우기)
    // 테스트가 도는 동안에는 인메모리 DB를 공유하기 때문에...
    // 단건은 괜찮지만 여러 건 한방에 할 때는 데이터가 공유됨 (application context가 죽기 전에는 살아있다)
    public void Setup() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    // springbootTest 사용시 repository 선언되어 있다면 삭제해야 함 mocking 사용안한다
    // 슬라이스 테스트는 web용 bean만 등록 respository는 bean으로 등록 안 함. -> repository bean .못 찾아서 에러남
    // 레포지토리 모킹해야 됨 = mock으로 만들어 달라

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    // 이 테스트는 도메인에 넣어도 좋다!
    // 비즈니스 로직 테스트. (도메인 객체에서 테스트)
    // 입력값이 제대로 들어오는 경우에만 test
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
                // 입력값 제한하기
                // id도 DB에 들어갈 때 값이 자동 생성되어야 하고, 값이 다 있으면 free도 true가 아니고... location이 있으니까 offline은 true가 맞는데 이런 식으로 집어넣을 수 있게 됨. 이러면 안 도미.
                .build()
                ;
        // mockbean return값 null이라서 발생하는 nullpointer exception
//        event.setId(10);
        // 스터빙
        // eventController.java 에서는 mocking을 안하는데 여기서는 mocking하니까 null이 들어감
        // slicing test 해제


        // 2023-04-17 헤더에 oauth token 넣어 주기
        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()) // access Token
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        // 요청 추가 (json 변환 후 본문 객체에 넣어 줌)
                        .content(objectMapper.writeValueAsString(eventDto))
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
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                // 근데 아마 여기서 깨질 것임....... 기본값이 false라고 나올 거라서 ㅎㅎ
                // 이벤트 만든 후에 update 함수 하나 불러서 free 값 조건에 맞게 변경 후 test.
                .andExpect(jsonPath("offline").value(true)) // location이 있으므로 true라고 나와야 한다
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name())) // 생성된 직후에는 draft 상태여야 함
                // 응답이 잘 생성됐을 경우 lnk 정보를 받을 수 있어야 함
                // link 정보가 없으므로 client는 전이를 할 수가 없다
                // link key값 다 맞춰 줘야 한다
                // docs로 확인할 예정이므로 삭제
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())

        // 최소 데이터 3개는 가지고 만들어야 함... 구현 전에 Test부터 만들어야 (TDD) 삼각정량법?? 측량법?

                // rest docs (test 돌릴 떄마다 snippet 덮어씌워지므로 기존 것들은 날아간다)
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
                        // manager 문서화가 안 돼 있어서... 일단 relaxed
                        relaxedResponseFields( // ResponseFields로 하게 되면 "_links"도 response의 일부이므로 검증을 거쳐야 한다. 그러므로 일부 field만 검증하려면 relaxed라는 prefix 사용
                                // 단, relaxed 접두어
                                // 문서 일부분만 테스트가 가능 -> 정확한 문서를 생성하지 못한다.
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


        ; // 세미콜론 여기 두기


    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    // from AuthServerConfigTest.java
    private String getAccessToken() throws Exception {
        // Given
//        String username = "ejpark@email.com"; // ejpark@fasoo.com 이 이미 있나? 그래서 unique result가 아니라고 error가 남...
//        String password = "ejpark";
        // Given (user 필요하므로 생성)

        // 여기엔 user 남겨둬야 함. (모든 테스트 실행/후마다 DB 지우도록 해놨으니까 여기는 save하게 해야 함)
        Account ejpark = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(ejpark);

//        String clientId = "myApp";
//        String clientSecret = "pass";

        // grant type이 중요하다
        // password라는 grand type으로 받을 것임
        // hop이 한 번!
        // 인증을 제공하는 그 서비스들이 만든 앱. (서드파티 X)
        // 인증 정보를 보유하고 있는 앱에서만 제공해야 하는 서비스
        // refactor -> introduce variable
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                        .param("username", appProperties.getUserUsername())
                        .param("password", appProperties.getUserPassword())
                        .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(responseBody).get("access_token").toString(); // object로 return하므로 string으로 변환

    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")

    // 입력값이 이상하게 들어오는 경우의 test
    public void createEvent_Bad_Request() throws Exception {

        // 이상한 값 막 넣어주면
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

        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        // 요청 추가 (json 변환 후 본문 객체에 넣어 줌)
                        .content(objectMapper.writeValueAsString(event))
                )// accept header를 통해 원하는 응답 알려줌. accept 헤더 지정하는 게 HTTP 응답에 더 맞는 듯.
                .andDo(print()) // 무슨 요청, 무슨 응답인지 확인 가능. MockHttpServletResponse: 출력 확인
                .andExpect(status().isBadRequest()) // 이상한 값 넣어주면 bad request가 나오길
                
                // 보내든 말든 입력값만 걸러서 받을 건지, 그냥 error를 발생시켜 버릴 건지는 결정에 따라 달린 일
                // 유연함 제공... 근데 값은 보낼 수 있나 보다... 하고 혼란을 줄 수 있기는 함 응답이 201이니까

        ; // 세미콜론 여기 두기

    }

    // 들어와야 할 값이 안 들어왔을 때
    @Test
    @TestDescription("입력값이 비어 있는 경우에 에러가 발생하는 테스트")

    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build(); // 빈값으로 보내기

        this.mockMvc.perform(post("/api/events") // 이거 하려면 늘 메서드에 exception 있어야 함
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    // 입력값이 들어오긴 하는데 값이 이상한 경우
    // annotaion으로 검증 어려움 -> validator 만들어서 검증
    /*
    주석이나 annotation이나 둘 다 달아서 test에 대한 설명...
    junit5: annotaion 제공하므로 그거 쓰면 됨. 그때는 이 annotation 이름 목록으로 나오게 된다.
     */
    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")

    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API development with Spring")
                // event 끝나는 날짜가 시작하는 날짜보다 빨라 버림 (이상한 값)
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 24, 22, 57))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 20, 22, 57))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                .endEventDateTime(LocalDateTime.of(2023, 03, 22, 22, 57))
                // max가 base보다 작다고? (이상한 값)
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events") // 이거 하려면 늘 메서드에 exception 있어야 함
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                // 응답 본문에 이런 값이 잇었으면 좋겠다. 어떤 요청을 보냈었는지, 어떤 응답코드를 받았는지...
                // eventResource로 감쌌더니 json array에 대해서는 unwrap이 작동 안해서 코드 수정함

                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
//                .andExpect(jsonPath("$[0].field").exists()) // global error의 경우엔 이게 없으므로 테스트 꺠질 수 있다.
        // 혹시 $[0].objectName이 존재하지 않는다고 하면 eventController로 가서 return 값에 error를 serialziation해서 담아주도록 되어 있는지 확인할 것.
        // 결과값 복사해서 jsonParser로 확인해 볼 것.

                // section 3: index
                // 에러가 발생했으면 에러를 받은 다음에 application 상태 전이가 index로만 가능함
                // index로 갈 수 있는 link 추가
                .andExpect(jsonPath("_links.index").exists());




        ; // 400 예상했는데 201 나와도 테스트 깨짐.
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 띄우는 상황, 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {

        // Given
        // 이벤트 30개 만들기
//        IntStream.range(0, 30).forEach(i -> {
//            this.generateEvent(i);
//        });

        // lambda refactored
        IntStream.range(0, 30).forEach(this::generateEvent);

        // refactored
//        ResultActions perform = this.mockMvc.perform(get("/api/events")
//                        .param("page", "1")
//                        .param("size", "10")
//                        .param("sort", "name,DESC")
//        );
//        perform.andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("page").exists())
//                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.profile").exists())
//                .andDo(document("query-events"))
//        ;


        // when
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())

                // 각각의 이벤트에 대한 link
                // item으로 갈 수 있는 link (클라이언트가 직접 입력할 필요 없이)
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())

                // self와 profile에 대한 link 확인
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists()) // profile에 대한 link를 만들려면 문서화해야 함

                // 문서화
                .andDo(document("query-events"))

                // TODO (문서화 더 해야 함)
                // page에 대한 정보: number: 1, links의 first는 첫 번째 페이지를 뜻한다... 이런 식으로 문서화 다 하면 됨
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 띄우는 상황, 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {

        // Given
        // 이벤트 30개 만들기
//        IntStream.range(0, 30).forEach(i -> {
//            this.generateEvent(i);
//        });

        // lambda refactored
        IntStream.range(0, 30).forEach(this::generateEvent); // 중단점 여기 찍고 디버거 실행

        // when
        this.mockMvc.perform(get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())

                // 각각의 이벤트에 대한 link
                // item으로 갈 수 있는 link (클라이언트가 직접 입력할 필요 없이)
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())

                // self와 profile에 대한 link 확인
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists()) // profile에 대한 link를 만들려면 문서화해야 함

                // 로그인한 상태면 이게 같이 오길
                .andExpect(jsonPath("_links.create-event").exists())

                // 문서화
                .andDo(document("query-events"))

        // TODO (문서화 더 해야 함)
        // page에 대한 정보: number: 1, links의 first는 첫 번째 페이지를 뜻한다... 이런 식으로 문서화 다 하면 됨
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 1개 조회하기")
    // 이 test를 디버그 모드로 실행해서 authentication 값 확인
    public void getEvent() throws Exception {
        // Given
        // 테스트를 위해서는 이벤트를 하나 생성해야 한다
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())

                // 문서화
                .andDo(document("get-an-event"))
        ;

        // 테스트가 어떻게 실패할지 예상하는 것도 즐거움... 404가 나올 것 같다든가 405가 나올 거 같다든가
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 응답 받기")
    public void getEvent404() throws Exception {

        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/11883", event.getId()))
                .andExpect(status().isNotFound());

    }

    // 이벤트 수정 API 테스트
    // 문서화 필수!!!!!
    // 이벤트 생성, 목록 조회, 단일 이벤트 조회 test가 전부 가능한 예제
    // 멈춰 놓고 직접 만들어 보기 (테스트 코드부터 구현하고)
    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        String eventName = "Updated Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class); // 이벤트에 있는걸 이벤트dto에 담기
        eventDto.setName(eventName);

        // When & Then (업데이트 요청)
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName)) // 수정한 이름으로 잘 반영되었는지
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event")) // request field  같은 snippet 추가하기
        ;

    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패") // dto는 있고 데이터도 유효하지만, 저런 id를 가진 event는 없으므로
    // 로직상에서 잘못된 값, 혹은 그냥 공백값
    public void updateEvent404() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        String eventName = "Updated Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);


        // When & Then (업데이트 요청)
        this.mockMvc.perform(put("/api/events/151232345")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    // 로직상에서 잘못된 값, 혹은 그냥 공백값
    public void updateEvent400_wrong() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        String eventName = "Updated Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then (업데이트 요청)
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION,  getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어 있는 경우 이벤트 수정 실패")
    // 로직상에서 잘못된 값, 혹은 그냥 공백값
    public void updateEvent400_empty() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        String eventName = "Updated Event";
        EventDto eventDto = new EventDto();

        // When & Then (업데이트 요청)
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                        .name("event " + index)
                        .description("test event")
                        .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 21, 22, 57))
                        .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 22, 22, 57))
                        .beginEventDateTime(LocalDateTime.of(2023, 03, 23, 22, 57))
                        .endEventDateTime(LocalDateTime.of(2023, 03, 24, 22, 57))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("강남역 D2 스타트업 팩토리")
                        .free(false)
                        .offline(true)
                        .eventStatus(EventStatus.DRAFT)
                        .build();

        return this.eventRepository.save(event);
    }


    // 테스트 다 실행하려면 메서드 밖에 커서 두게 하고 ctrl shift F10 -> 클래스 안에 있는 모든 테스트 메서드 실행

}
