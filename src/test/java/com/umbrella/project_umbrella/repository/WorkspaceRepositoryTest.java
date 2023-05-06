package com.umbrella.project_umbrella.repository;

import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class WorkspaceRepositoryTest {

    @Autowired
    WorkSpaceRepository workSpaceRepository;

    @Autowired
    EntityManager entityManager;

    private static String email = "test@test.com";
    private static String password = "12345";
    private static String name = "홍길동";
    private static String nickname = "테스트계정";

    private static String title = "Test Workspace";
    private static String description = "테스트를 위한 워크스페이스입니다.";


    private User createUser(int i) {
        User theUser = User.builder()
                .email(email + i)
                .password(password)
                .name(name)
                .nickName(nickname + i)
                .role(Role.USER)
                .age(22)
                .gender(Gender.MALE)
                .build();

        return theUser;
    }

    private WorkSpace createWorkspace(int i) {
        WorkSpace theWorkspace = WorkSpace.builder()
                .title(title + i)
                .description(description)
                .build();

        return theWorkspace;
    }

    @Test
    public void createWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);

        WorkSpace savedWorkspace = workSpaceRepository.save(theWorkspace);

        WorkSpace findWorkSpace = workSpaceRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("워크스페이스가 존재하지 않습니다!")
        );

        assertThat(findWorkSpace).isSameAs(savedWorkspace);
        assertThat(findWorkSpace).isSameAs(theWorkspace);
    }

    @Test
    public void findWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);

        WorkSpace savedWorkspace = workSpaceRepository.save(theWorkspace);

        WorkSpace findWorkSpace = workSpaceRepository.findByTitle(title + 0).orElseThrow(
                () -> new RuntimeException("워크스페이스가 존재하지 않습니다!")
        );

        assertThat(findWorkSpace).isSameAs(savedWorkspace);
        assertThat(findWorkSpace).isSameAs(theWorkspace);
    }

    @Test
    public void deleteWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);

        WorkSpace savedWorkspace = workSpaceRepository.save(theWorkspace);

        workSpaceRepository.delete(savedWorkspace);

        assertThrows(Exception.class, () -> workSpaceRepository.findByTitle(savedWorkspace.getTitle())
                .orElseThrow(EntityNotFoundException::new));
    }
}
