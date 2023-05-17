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
import java.time.LocalTime;
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
    public Map<String, Map<LocalTime, Integer>> getScheduleInEvent(List<Schedule> schedules) {
        Map<String, Map<LocalTime, Integer>> timeBlockInSchedule = new HashMap<>();

        for (Schedule schedule : schedules) {
            String date = schedule.getDate().toString().substring(0, 10);
            List<LocalTime> timeBlocks = schedule.getTimeBlocks();

            Map<LocalTime, Integer> timeBlockCountMap = timeBlockInSchedule.getOrDefault(date, new HashMap<>());
            for (LocalTime timeBlock : timeBlocks) {
                int count = timeBlockCountMap.getOrDefault(timeBlock, 0);
                timeBlockCountMap.put(timeBlock, count + 1);
            }
            timeBlockInSchedule.put(date, timeBlockCountMap);
        }

        return timeBlockInSchedule;
    }

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
        String loginUserName = securityUtil.getLoginUserNickname();
        Event theEvent = validateEvent(uuid, loginUserName);
        eventRepository.delete(theEvent);

        return theEvent.getUuid().toString();
    }

    @Override
    public Schedule addSchedule(UUID uuid, RequestScheduleDto requestScheduleDto) {
        String scheduleMember = securityUtil.getLoginUserNickname();
        Event theEvent = validateEvent(uuid, scheduleMember);
        Instant scheduleDate = requestScheduleDto.getDate().toInstant().truncatedTo(ChronoUnit.DAYS);

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .member(scheduleMember)
                .event(theEvent)
                .date(Date.from(scheduleDate))
                .timeBlocks(requestScheduleDto.getTimeBlocks())
                .build());

        return savedSchedule;
    }

    private Event validateEvent(UUID uuid, String userNickName) {
        Optional<Event> optionalEvent = eventRepository.findEventByUuid(uuid);
        if (optionalEvent.isEmpty()) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }
        if (!optionalEvent.get().getMembers().contains(userNickName)) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }
        return optionalEvent.get();
    }
}
