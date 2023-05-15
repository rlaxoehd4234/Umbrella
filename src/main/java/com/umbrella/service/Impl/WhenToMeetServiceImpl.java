package com.umbrella.service.Impl;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.EventRepository;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.domain.WhenToMeet.ScheduleRepository;
import com.umbrella.domain.exception.WhenToMeetException;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.WhenToMeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.umbrella.domain.exception.WhenToMeetExceptionType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class WhenToMeetServiceImpl implements WhenToMeetService {

    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;
    private final SecurityUtil securityUtil;

    @Override
    public Event createEvent(RequestEventDto requestEventDto) {
        try {
            Instant nowInstant = (new Date()).toInstant().truncatedTo(ChronoUnit.DAYS);
            Instant startInstant = requestEventDto.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS);
            Instant endInstant = requestEventDto.getEndDate().toInstant().truncatedTo(ChronoUnit.DAYS);

            if ((!startInstant.equals(nowInstant) && !startInstant.isAfter(nowInstant)) || !endInstant.isAfter(startInstant)) {
                throw new WhenToMeetException(ILLEGAL_DATE_RANGE_ERROR);
            }

            Event newEvent = eventRepository.save(
                    Event.builder()
                            .title(requestEventDto.getTitle())
                            .startDate(Date.from(startInstant))
                            .endDate(Date.from(endInstant))
                            .members(requestEventDto.getMembers())
                            .build()
            );
            return newEvent;
        } catch (Exception e) {
            throw new WhenToMeetException(DEFAULT_WHEN2MEET_ERROR);
        }
    }

    @Override
    public String deleteEvent(UUID uuid) {
        Optional<Event> optionalEvent = validateEvent(uuid);
        eventRepository.delete(optionalEvent.get());

        return optionalEvent.get().getUuid().toString();
    }

    @Override
    public Schedule addSchedule(UUID uuid, RequestScheduleDto requestScheduleDto) {
        String scheduleMember = securityUtil.getLoginUserNickname();
        Optional<Event> optionalEvent = validateEvent(uuid);
        Instant scheduleDate = requestScheduleDto.getDate().toInstant().truncatedTo(ChronoUnit.DAYS);

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .member(scheduleMember)
                .event(optionalEvent.get())
                .date(Date.from(scheduleDate))
                .timeBlocks(requestScheduleDto.getTimeBlocks())
                .build());

        return savedSchedule;
    }

    private Optional<Event> validateEvent(UUID uuid) {
        Optional<Event> optionalEvent = eventRepository.findEventByUuid(uuid);
        if (optionalEvent.isEmpty()) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }
        if (!optionalEvent.get().getMembers().contains(securityUtil.getLoginUserNickname())) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }
        return optionalEvent;
    }
}
