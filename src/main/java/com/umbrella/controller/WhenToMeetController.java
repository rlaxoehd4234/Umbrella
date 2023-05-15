package com.umbrella.controller;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.EventRepository;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;
import com.umbrella.service.WhenToMeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class WhenToMeetController {

    private final EventRepository eventRepository;
    private final WhenToMeetService whenToMeetService;

    @GetMapping(value = "/event/{uuid}")
    public ResponseEntity getEventByUUID(@PathVariable("uuid")UUID uuid) {
        Optional<Event> theEvent = eventRepository.findEventByUuid(uuid);
        return theEvent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/event")
    public ResponseEntity createEvent(@RequestBody RequestEventDto requestEventDto) {
        Event theEvent = whenToMeetService.createEvent(requestEventDto);
        return ResponseEntity.ok().body(theEvent.getUuid());
    }

    @DeleteMapping("/event/{uuid}/delete")
    public ResponseEntity deleteEvent(@PathVariable("uuid") UUID uuid) {
        String theUUID = whenToMeetService.deleteEvent(uuid);
        return ResponseEntity.ok().body(theUUID);
    }

    @PostMapping("/event/{uuid}/schedule")
    public ResponseEntity addSchedule(@PathVariable("uuid") UUID uuid, @RequestBody RequestScheduleDto requestScheduleDto) {
        Schedule savedSchedule = whenToMeetService.addSchedule(uuid, requestScheduleDto);
        return ResponseEntity.ok().body(savedSchedule.getDate());
    }
}
