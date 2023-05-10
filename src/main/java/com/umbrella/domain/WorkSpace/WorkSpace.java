package com.umbrella.domain.WorkSpace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    @NotBlank
    private String description;

    @OneToMany(mappedBy = "workspace")
    private List<WorkspaceUser> workspaceUsers = new ArrayList<>();

    @Builder
    public WorkSpace(String title, String description){
        this.title = title;
        this.description = description;
    }

    public void update(String title, String description){
        this.title = title;
        this.description = description;
    }
}
