package com.umbrella.domain.WhenToMeet;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Entity(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull
    @Column(name = "schedule_member")
    private Long userId;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Column(name = "time_block")
    @ElementCollection
    @DateTimeFormat(pattern = "HH:mm")
    private List<LocalTime> timeBlocks;

    @Builder
    public Schedule(Event event, Long userId, Date date, List<LocalTime> timeBlocks) {
        this.event = event;
        this.userId = userId;
        this.date = date;
        this.timeBlocks = timeBlocks;
    }
}
