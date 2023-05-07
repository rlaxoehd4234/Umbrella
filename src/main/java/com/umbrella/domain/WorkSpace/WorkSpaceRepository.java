package com.umbrella.domain.WorkSpace;

import com.umbrella.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace,Long> {

    Optional<WorkSpace> findById(Long id);

    Optional<WorkSpace> findByTitle(String title);

    Optional<WorkSpace> findByIdAndTitle(Long id, String title);
}
