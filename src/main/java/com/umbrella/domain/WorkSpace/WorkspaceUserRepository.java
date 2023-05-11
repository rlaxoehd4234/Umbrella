package com.umbrella.domain.WorkSpace;

import com.umbrella.domain.User.User;
import com.umbrella.dto.workspace.WorkspaceListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {

    List<WorkspaceUser> findByWorkspaceUser(User workspaceUser);

    List<WorkspaceUser> findByWorkspace(WorkSpace workSpace);

    Optional<WorkspaceUser> findByWorkspaceUserAndWorkspace(User workspaceUser, WorkSpace workSpace);
}
