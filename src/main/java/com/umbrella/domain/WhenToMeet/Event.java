package com.umbrella.domain.WhenToMeet;

import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
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
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "uuid", length = 36, updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

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

    @Setter
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Column(name = "schedules")
    private List<Schedule> schedules;

    @Builder
    public Event(WorkSpace workSpace, String title, Date startDate, Date endDate) {
        this.workSpace = workSpace;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
