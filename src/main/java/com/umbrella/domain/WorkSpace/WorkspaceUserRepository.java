package com.umbrella.domain.WorkSpace;

import com.umbrella.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {
    List<WorkspaceUser> findByWorkspaceUser(User workspaceUser);

    List<WorkspaceUser> findByWorkspace(WorkSpace workSpace);
}
