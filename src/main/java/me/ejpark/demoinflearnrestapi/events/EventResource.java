package me.ejpark.demoinflearnrestapi.events;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);

        // add(new Link("http://localhost:8080/api/events" + event.getId());
        // 위와 아래는 같은 내용이지만 위는 type-safe 하지 않음
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }


}
