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
    @Column(name = "board_id")
    private Long id;

    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @Builder
    public Board(String title){
        this.title = title;
    }
    public void update(String title){
        this.title = title;
    }
}
