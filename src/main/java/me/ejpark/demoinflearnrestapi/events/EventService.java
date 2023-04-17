package me.ejpark.demoinflearnrestapi.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

//    public List<Event> fetchFilteredEventDataAsList() {
//        return eventRepository.findByBasePrice();
//    }
}
