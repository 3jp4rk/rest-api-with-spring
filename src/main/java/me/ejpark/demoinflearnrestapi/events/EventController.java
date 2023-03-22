package me.ejpark.demoinflearnrestapi.events;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
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

    private final ModelMapper modelMapper;

    // 생성자가 하나만 있고 생성자로 받아올 파라미터가 bean으로 등록되어 있으면 autowired 생략해도 됨 (spring 4.3부터)
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    // event repository
    @PostMapping
//    public ResponseEntity createEvent(@RequestBody Event event) {
//
//        Event newEvent = this.eventRepository.save(event);
//
//        // 함수 파라미터 추가했기 때문에 createEvent(null) 해줘야 하는데 이게 귀찮으니까
//        // @RequestMapping으로 간다
//        // PostMapping에서 url 뺐으니까 methodOn, createClass 제외
////        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
//        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri(); // 저장한 newEvent객체의 id
//
////        event.setId(10); // 아이디 임의 생성해서 보내기
////        return ResponseEntity.created(createdUri).build(); // 201 응답 생성
//        return ResponseEntity.created(createdUri).body(event); // 201 응답 생성 -> repository 추가 시 return값 이렇게 바꿔야
//    }
    // DTO 사용
    // @Valid: request에 들어있는 값을 dto에  binding 할 떄 검증 수행
    // Errors에 검증 결과 넣어줌
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) { // 입력값에 id, free가 있어도 무시하게 됨. 받기로 명시한 값들만 들어오게 됨

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Event event = modelMapper.map(eventDto, Event.class); // eventdto -> event 변환
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        // newEvent에서 null이 뜨는 이유? mocking했는데 왜지?
        // save가 호출될 떄 object를 받은 경우에 객체 return.
        // test code에서 save에 전달한 객체는 메서드 안에서 새로 만든 객체. test code의 모킹 객체가 아님.
        // null이 return됨. null.getid라서 에러가 나 버림...
        return ResponseEntity.created(createdUri).body(event);

    }
}
