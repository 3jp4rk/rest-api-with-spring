package me.ejpark.demoinflearnrestapi.events;

import me.ejpark.demoinflearnrestapi.accounts.Account;
import me.ejpark.demoinflearnrestapi.accounts.AccountAdapter;
import me.ejpark.demoinflearnrestapi.accounts.CurrentUser;
import me.ejpark.demoinflearnrestapi.common.ErrorsResource;
import org.bouncycastle.util.Times;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

//    private final EventService eventService;

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
    // DTO 사용
    // @Valid: request에 들어있는 값을 dto에  binding 할 떄 검증 수행
    // Errors에 검증 결과 넣어줌
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, // 입력값에 id, free가 있어도 무시하게 됨. 받기로 명시한 값들만 들어오게 됨
                                      Errors errors,
                                      @CurrentUser Account currentUser) {


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

        // event manager 정보를 현재 user로 setting
        event.setManager(currentUser);


        Event newEvent = this.eventRepository.save(event);
        // 위의 두 줄은 service class에 보내는 것도 괜찮음 .


        // 이 linkto가 spring hateoas가 제공하는 link 생성 기능의 일부
        // link = new link(""); 해서 만들 수 있음.
        // controller mapping 정보 가져와서 만드는 방법.

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

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

    // 과제 추가
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @RequestParam(defaultValue="false") Boolean basePriceFilter,
                                      @RequestParam(defaultValue="false") Boolean EnrollmentDatetimeFilter,
                                      @CurrentUser Account account) { // 바로 주입받기 가능

        //meta annotation 지원!
        // @AuthenticationPrincipal(expression="account") 간추리기 가능
//        Page<Event> page = this.eventRepository.findAll(pageable);

        Page<Event> page = null;
        if (basePriceFilter) {
            int minPrice = 100;
            int maxPrice = 200;
            page = this.eventRepository.findByBasePriceBetween(minPrice, maxPrice, pageable);
        }
        else if (EnrollmentDatetimeFilter) {
            LocalDateTime now = LocalDateTime.now();
            page = this.eventRepository.findByCloseEnrollmentDateTimeBefore(now, pageable);
        }

        else {
            page = this.eventRepository.findAll(pageable);
        }


        // 각각의 이벤트 리소스로 변경
        var pagedResources = assembler.toResource(page, e -> new EventResource(e));

        // profile link 추가 (뭐든 resource로 변환하고 나면 link를 추가할 수 있는 method가 생긴다)
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        if (account != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event")); // 이 link 추가해 줘
        }

        // 이벤트 생성 시에는 현재 사용자 정보를 event에 주입해서 비교해 줘야 한다 (manager가 맞는지)


        return ResponseEntity.ok(pagedResources);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {

        // anonymous로 접근하는 곳
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        // 만약 없으면 404
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        // profile link 추가
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile")); // index.adoc에 정의된 스펙대로

        // currentUser == manager
        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
            // 이벤트 매니저인 경우에만 이벤트 수정 가능한 링크 보내주기
        }


        return ResponseEntity.ok(eventResource);

    }


    // update test
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors, // dto validation  수행하고 그 결과는 errors에 담아
                                      @CurrentUser Account currentUser) {
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

        // event를 가져왔는데 만약에 현재 접속자가 manager가 아닌 user일 경우
        if (!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        }

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    private static ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
