package com.umbrella.service.Impl;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.EventRepository;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.domain.WhenToMeet.ScheduleRepository;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.exception.WhenToMeetException;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.WhenToMeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.umbrella.domain.exception.WhenToMeetExceptionType.*;
import static com.umbrella.domain.exception.WorkspaceExceptionType.NOT_FOUNT_WORKSPACE;

@Service
@Transactional
@RequiredArgsConstructor
public class WhenToMeetServiceImpl implements WhenToMeetService {

    private final WorkSpaceRepository workSpaceRepository;
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
        Optional<WorkSpace> optionalWorkspace  = workSpaceRepository.findById(requestEventDto.getWorkspaceId());
        if (optionalWorkspace.isEmpty()) {
            throw new WorkspaceException(NOT_FOUNT_WORKSPACE);
        }
        WorkSpace theWorkspace = optionalWorkspace.get();

        Instant nowInstant = (new Date()).toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant startInstant = requestEventDto.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant endInstant = requestEventDto.getEndDate().toInstant().truncatedTo(ChronoUnit.DAYS);

        if ((!startInstant.equals(nowInstant) && !startInstant.isAfter(nowInstant)) || !endInstant.isAfter(startInstant)) {
            throw new WhenToMeetException(ILLEGAL_DATE_RANGE_ERROR);
        }
        try {
            Event newEvent = eventRepository.save(
                    Event.builder()
                            .workSpace(theWorkspace)
                            .title(requestEventDto.getTitle())
                            .startDate(Date.from(startInstant))
                            .endDate(Date.from(endInstant))
                            .build()
            );

            return newEvent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WhenToMeetException(DEFAULT_WHEN2MEET_ERROR);
        }
    }

    @Override
    public String deleteEvent(UUID uuid) {
        Long loginUserId = securityUtil.getLoginUserId();
        Event theEvent = validateEvent(uuid, loginUserId);
        eventRepository.delete(theEvent);

        return theEvent.getUuid().toString();
    }

    @Override
    public Schedule addSchedule(UUID uuid, RequestScheduleDto requestScheduleDto) {
        Long loginUserId = securityUtil.getLoginUserId();
        Event theEvent = validateEvent(uuid, loginUserId);

        return generateSchedule(requestScheduleDto, loginUserId, theEvent);
    }

    @Override
    public Schedule modifySchedule(UUID uuid, RequestScheduleDto requestScheduleDto) {
        Long loginUserId = securityUtil.getLoginUserId();
        Event theEvent = validateEvent(uuid, loginUserId);
        scheduleRepository.deleteByEventAndUserId(theEvent, loginUserId);

        return generateSchedule(requestScheduleDto, loginUserId, theEvent);
    }

    private Schedule generateSchedule(RequestScheduleDto requestScheduleDto, Long loginUserId, Event theEvent) {
        Instant scheduleDate = requestScheduleDto.getDate().toInstant().truncatedTo(ChronoUnit.DAYS);

        return scheduleRepository.save(Schedule.builder()
                .event(theEvent)
                .userId(loginUserId)
                .date(Date.from(scheduleDate))
                .timeBlocks(requestScheduleDto.getTimeBlocks())
                .build());
    }

    private Event validateEvent(UUID uuid, Long loginUserId) {
        Optional<Event> optionalEvent = eventRepository.findEventByUuid(uuid);
        if (optionalEvent.isEmpty()) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }
        Event theEvent = optionalEvent.get();
        if (theEvent.getWorkSpace().getWorkspaceUsers().stream().anyMatch(workspaceUser -> {
            return !(workspaceUser.getWorkspaceUser().getId() == (loginUserId));
        })) {
            throw new WhenToMeetException(NOT_FOUND_EVENT_ERROR);
        }

        return theEvent;
    }
}
