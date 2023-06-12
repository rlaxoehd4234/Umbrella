package com.umbrella.domain.WhenToMeet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByUuid(UUID uuid);
}
