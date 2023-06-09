package com.umbrella.service;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WhenToMeetService {

    Map<String, Map<LocalTime, Integer>> getScheduleInEvent(List<Schedule> schedules);

    Event createEvent(RequestEventDto requestEventDto);

    String deleteEvent(UUID uuid);

    void addSchedule(UUID uuid, List<RequestScheduleDto> requestScheduleDto);

    void modifySchedule(UUID uuid, List<RequestScheduleDto> requestScheduleDto);
}
