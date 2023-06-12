package com.umbrella.domain.Board;

import com.umbrella.domain.WorkSpace.WorkSpace;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_id")
    private Long id;

    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @Builder
    public Board(String title, WorkSpace workSpace){
        this.title = title;
        this.workSpace = workSpace;
    }
    public void update(String title){
        this.title = title;
    }
}
