package com.umbrella.controller;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.EventRepository;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.domain.exception.WhenToMeetException;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.WhenToMeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.umbrella.domain.exception.WhenToMeetExceptionType.NOT_FOUND_EVENT_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class WhenToMeetController {

    private final EventRepository eventRepository;
    private final WhenToMeetService whenToMeetService;

    @GetMapping(value = "/event/{uuid}")
    public ResponseEntity<Map<String, Map<LocalTime, Integer>>> getEventTimeBlockMapByUUID(@PathVariable("uuid") UUID uuid) {
        Optional<Event> optionalEvent = eventRepository.findEventByUuid(uuid);
        if (optionalEvent.isEmpty()) {
           throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }

        Event theEvent = optionalEvent.get();
        List<Schedule> schedules = theEvent.getSchedules();

        Map<String, Map<LocalTime, Integer>> timeBlockInSchedule = whenToMeetService.getScheduleInEvent(schedules);
        return ResponseEntity.ok(timeBlockInSchedule);
    }

    @PostMapping(value= "/event")
    public ResponseEntity createEvent(@RequestBody RequestEventDto requestEventDto) {
        Event theEvent = whenToMeetService.createEvent(requestEventDto);
        return ResponseEntity.ok().body(theEvent.getUuid());
    }

    @DeleteMapping(value = "/event/{uuid}/delete")
    public ResponseEntity deleteEvent(@PathVariable("uuid") UUID uuid) {
        String theUUID = whenToMeetService.deleteEvent(uuid);
        return ResponseEntity.ok().body(theUUID);
    }

    @PostMapping(value = "/event/{uuid}/schedule")
    public ResponseEntity addSchedule(@PathVariable("uuid") UUID uuid, @RequestBody RequestScheduleDto requestScheduleDto) {
        Schedule savedSchedule = whenToMeetService.addSchedule(uuid, requestScheduleDto);
        return ResponseEntity.ok().body(savedSchedule.getDate());
    }
}
