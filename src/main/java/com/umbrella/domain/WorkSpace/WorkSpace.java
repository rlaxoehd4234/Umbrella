package com.umbrella.domain.WorkSpace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkSpace {

    @Id 
    @Column(name = "workspace_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 50)
    private String title;

    @Builder
    public WorkSpace(String title){
        this.title = title;
    }

    public void update(String title){
        this.title = title;
    }

}
