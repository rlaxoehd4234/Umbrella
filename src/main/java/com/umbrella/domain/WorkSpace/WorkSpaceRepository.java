package com.umbrella.domain.WorkSpace;

import com.umbrella.dto.workspace.WorkspaceResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace,Long> {

    Optional<WorkSpace> findById(Long id);

    Optional<WorkSpace> findByTitle(String title);

    Optional<WorkSpace> findByIdAndTitle(Long id, String title);
}
