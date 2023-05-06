package com.umbrella.domain.WhenToMeet;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Entity(name = "event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private long id;

    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", columnDefinition = "VARCHAR(255)", updatable = false, nullable = false)
    private UUID uuid;

    @NotEmpty
    @Column(name = "title")
    private String title;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotEmpty
    @ElementCollection
    @Column(name = "event_members")
    private List<String> members;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Column(name = "schedules")
    private List<Schedule> schedules;

    @Builder
    public Event(String title, Date startDate, Date endDate, List<String> members) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.members = members;
    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
