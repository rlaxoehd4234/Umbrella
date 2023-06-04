package com.umbrella.domain.WorkSpace;

import com.umbrella.domain.User.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class WorkspaceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "workspace_user_id")
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkSpace workspace;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User workspaceUser;

    public void takeWorkspace(WorkSpace workspace) {
        this.workspace = workspace;
        workspace.getWorkspaceUsers().add(this);
    }
}
