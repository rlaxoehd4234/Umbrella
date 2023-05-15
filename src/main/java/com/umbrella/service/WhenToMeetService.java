package com.umbrella.service;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WhenToMeet.Schedule;
import com.umbrella.dto.whenToMeet.RequestEventDto;
import com.umbrella.dto.whenToMeet.RequestScheduleDto;

import java.util.UUID;

public interface WhenToMeetService {

    Event createEvent(RequestEventDto requestEventDto);

    String deleteEvent(UUID uuid);

    Schedule addSchedule(UUID uuid, RequestScheduleDto requestScheduleDto);
}
