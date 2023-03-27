package me.ejpark.demoinflearnrestapi.events;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

//
//public class EventResource extends ResourceSupport {
//
//    @JsonUnwrapped // 이렇게 해야 "event": {"id":} 이래서 id값을 못 찾는 에러가 사라짐! event로 감싸는 걸 unwrap해서 그냥 {"id": } 이렇게 시작하게 해야 함
//    private Event event;
//
//    public EventResource(Event event) {
//        this.event = event;
//    }
//
//    public Event getEvent() {
//        return event;
//    }
//}


public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);

        // add(new Link("http://localhost:8080/api/events" + event.getId());
        // 위와 아래는 같은 내용이지만 위는 type-safe 하지 않음
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }


}
