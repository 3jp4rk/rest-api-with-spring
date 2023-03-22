package me.ejpark.demoinflearnrestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)

public class EventController {
//    @PostMapping("/api/events")
//    public ResponseEntity createEvent(@RequestBody Event event) {

    // EventRepository class 생성 후 주입
    private final EventRepository eventRepository;

//    private final ModelMapper modelMapper;

    // 생성자가 하나만 있고 생성자로 받아올 파라미터가 bean으로 등록되어 있으면 autowired 생략해도 됨 (spring 4.3부터)
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
//        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {

        Event newEvent = this.eventRepository.save(event);

        // 함수 파라미터 추가했기 때문에 createEvent(null) 해줘야 하는데 이게 귀찮으니까
        // @RequestMapping으로 간다
        // PostMapping에서 url 뺐으니까 methodOn, createClass 제외
//        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri(); // 저장한 newEvent객체의 id

//        event.setId(10); // 아이디 임의 생성해서 보내기
//        return ResponseEntity.created(createdUri).build(); // 201 응답 생성
        return ResponseEntity.created(createdUri).body(event); // 201 응답 생성 -> repository 추가 시 return값 이렇게 바꿔야

    }
    // DTO 사용
//    public ResponseEntity createEvent(@RequestBody EventDto eventDto) { // 입력값에 id, free가 있어도 무시하게 됨. 받기로 명시한 값들만 들어오게 됨
//
////        Event newEvent = this.eventRepository.save(event); // eventDto -> event로 바꿔줘야 레포지토리 사용 가능. ModelMapper 사용. 사이트 가서 예제 확인.
//
////        // 원래대로라면 .eventDto. 값을 event로 옮겨야 함
////        Event event = Event.builder()
////                .name(eventDto.getName())
////                .description(eventDto.getDescription()).build();
//
//        // 이 과정을 생략 가능함: ModelMapper
//        // maven repository 가서 최신버전 받아오기. (2.3.1) mavenRepository  가서 modelMapper 검색 -> 버전 선택 -> pom.xml 코드 블록 복사 -> postgre 밑에 붙여넣기
//        // 우측 maven 탭 -> dependencies에 modelmapper 들어온 거 확인. 공용 객체이므로 bean으로 등록
//        Event event = modelMapper.map(eventDto, Event.class);
//        Event newEvent = this.eventRepository.save(event);
//        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
//        // newEvent에서 null이 뜨는 이유? mocking했는데 왜지?
//        // save가 호출될 떄 object를 받은 경우에 객체 return.
//        // test code에서 save에 전달한 객체는 메서드 안에서 새로 만든 객체. test code의 모킹 객체가 아님.
//        // null이 return됨. null.getid라서 에러가 나 버림...
//        return ResponseEntity.created(createdUri).body(event);
//
//
//
//
//    }
}
