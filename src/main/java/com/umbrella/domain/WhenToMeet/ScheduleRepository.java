package com.umbrella.domain.WhenToMeet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    void deleteByEventAndMember(Event event, String member);
}
