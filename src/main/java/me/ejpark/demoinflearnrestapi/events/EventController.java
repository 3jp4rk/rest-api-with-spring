package me.ejpark.demoinflearnrestapi.events;

import me.ejpark.demoinflearnrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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

    private final EventValidator eventValidator;

    // 생성자가 하나만 있고 생성자로 받아올 파라미터가 bean으로 등록되어 있으면 autowired 생략해도 됨 (spring 4.3부터)
    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;

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
//            return ResponseEntity.badRequest().body(errors);
            return badRequest(errors);

        }


        // validator
        // 여기에 error 코드 같은 거 다 들어있다. debug해서 확인해보기
        // errors 0, 1, 2 들어가서 확인
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors); // body에 event는 담을 수 있었지만
            // body에 error를 담을 수는 없다... event처럼 json으로 안 나감.
            // error 객체를 json으로 변환할 수 없기 떄문.
            // java bean 스펙을 준수하고 있는 객체가 아니므로 변환할 수 없다.

        }

        Event event = modelMapper.map(eventDto, Event.class); // eventdto -> event 변환

        // 들어온 값에 따라 free update (무료인지 아닌지)
        event.update();
        Event newEvent = this.eventRepository.save(event);
        // 위의 두 줄은 service class에 보내는 것도 괜찮음 .


        // 이 linkto가 spring hateoas가 제공하는 link 생성 기능의 일부
        // link = new link(""); 해서 만들 수 있음.
        // controller mapping 정보 가져와서 만드는 방법.

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        // newEvent에서 null이 뜨는 이유? mocking했는데 왜지?
        // save가 호출될 떄 object를 받은 경우에 객체 return.
        // test code에서 save에 전달한 객체는 메서드 안에서 새로 만든 객체. test code의 모킹 객체가 아님.
        // null이 return됨. null.getid라서 에러가 나 버림...

        // hateoas
        // event -> eventResource로 변환
        // alt option command v
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
//        eventResource.add(selfLinkBuilder.withSelfRel()); // self relation으로 추가 (eventResource에서 만드는 걸로 변경)
        eventResource.add(selfLinkBuilder.withRel("update-event")); // link 자체는 같아도 method가 다름 (put)
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));


        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        // page에 대한 link 정보 (이전 페이지, 다음 페이지, 첫 페이지 등에 대한 정보 )
        Page<Event> page = this.eventRepository.findAll(pageable);
//        PagedResources<Resource<Event>> pagedResources = assembler.toResource(page);
        // 너무 길어서 var로 단축.
//        var pagedResources = assembler.toResource(page);
        // 각각의 이벤트 리소스로 변경
        var pagedResources = assembler.toResource(page, e -> new EventResource(e));

        // profile link 추가 (뭐든 resource로 변환하고 나면 link를 추가할 수 있는 method가 생긴다)
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        // 만약 없으면 404
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        // profile link 추가
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile")); // index.adoc에 정의된 스펙대로
        return ResponseEntity.ok(eventResource);

    }


    // update test
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) { // dto validation  수행하고 그 결과는 errors에 담아서
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 안 비어 있으면 꺼내서 사용 가능
        if (errors.hasErrors()) {
            // binding할 때 에러가 있다는 얘기는 notnull, 뭐 이런 애노테이션에 걸렸다는 뜻
            return badRequest(errors);
        }

        // 비즈니스 로직 검사
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // 여기까지도 문제가 없으면 update 가능해짐
        Event existingEvent = optionalEvent.get();
//        existingEvent.setName(eventDto.getName()); // 이걸 쭉 MODELmAPPER가 다 해줌
        this.modelMapper.map(eventDto, existingEvent); // src, dest 순서. eventDto의 값을 기존 event값에 덮어씌우는 것임
        this.eventRepository.save(existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    private static ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
