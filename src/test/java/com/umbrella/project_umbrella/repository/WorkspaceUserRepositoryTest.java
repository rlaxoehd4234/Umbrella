package com.umbrella.project_umbrella.repository;

import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.dto.user.UserUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class WorkspaceUserRepositoryTest {

    @Autowired
    WorkspaceUserRepository workspaceUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkSpaceRepository workSpaceRepository;

    @Autowired
    EntityManager em;

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
    public void createWorkspaceUser() {
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();
        WorkspaceUser savedWorkspaceUser = workspaceUserRepository.save(theWorkspaceUser);

        System.out.println(savedWorkspaceUser.getWorkspace());

        WorkspaceUser findWorkspaceUser = workspaceUserRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("해당 엔티티가 존재하지 않습니다.")
        );

        assertThat(findWorkspaceUser).isSameAs(savedWorkspaceUser);
        assertThat(findWorkspaceUser).isSameAs(theWorkspaceUser);
    }

    @Test
    public void createWorkspaceUserAndAddUserAndWorkspace() {
        User theUser = createUser(0);
        em.persist(theUser);
        WorkSpace theWorkspace = createWorkspace(0);
        em.persist(theWorkspace);
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);
        em.persist(theWorkspaceUser);

        workspaceUserRepository.save(theWorkspaceUser);
        em.flush();

        List<WorkspaceUser> findWorkspaceUser = workspaceUserRepository.findByWorkspaceUser(theUser);

        assertThat(findWorkspaceUser.get(0).getWorkspaceUser().getEmail()).isEqualTo(theUser.getEmail());
        assertThat(findWorkspaceUser.get(0).getWorkspace().getTitle()).isEqualTo(theWorkspace.getTitle());
    }

    @Test
    public void updateElementInWorkspaceUserTest() {
        User theUser = createUser(0);
        em.persist(theUser);
        WorkSpace theWorkspace = createWorkspace(0);
        em.persist(theWorkspace);
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);
        em.persist(theWorkspaceUser);

        workspaceUserRepository.save(theWorkspaceUser);
        em.flush();
        em.clear();

        Optional<String> updateNickName = Optional.of("updateNickName");

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name(Optional.empty())
                .nickName(updateNickName)
                .age(Optional.empty())
                .build();

        User updateUser = userRepository.findByEmail(theUser.getEmail()).orElseThrow(EntityNotFoundException::new);
        updateUser.updateUser(userUpdateDto);
        userRepository.save(updateUser);

        List<WorkspaceUser> updatedWorkspaceUser = workspaceUserRepository.findByWorkspaceUser(updateUser);
        WorkSpace updatedWorkspace = workSpaceRepository.findByTitle(theWorkspace.getTitle()).orElseThrow(
                () -> new EntityNotFoundException()
        );

        assertThat(
                updatedWorkspaceUser.get(0).getWorkspaceUser().getNickName()
        ).isEqualTo(updateUser.getNickName());
        assertThat(
                updatedWorkspace.getWorkspaceUsers().get(0).getWorkspaceUser().getNickName()
        ).isEqualTo(updateUser.getNickName());
    }

    @Test
    public void deleteElementInWorkspaceUser() {
        User theUser = createUser(0);
        em.persist(theUser);
        WorkSpace theWorkspace = createWorkspace(0);
        em.persist(theWorkspace);
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);
        em.persist(theWorkspaceUser);

        workspaceUserRepository.save(theWorkspaceUser);
        em.flush();
        em.clear();

        theUser.exitWorkspaceUser(theWorkspaceUser);
        workspaceUserRepository.delete(theWorkspaceUser);
        em.flush();
        em.clear();

        assertThat(theUser.getWorkspaceUsers().size()).isEqualTo(0);
        assertThat(theWorkspace.getWorkspaceUsers().size()).isEqualTo(0);
        assertThat(workspaceUserRepository.findByWorkspaceUser(theUser).stream().findFirst()).isEmpty();
        assertThat(workspaceUserRepository.findByWorkspace(theWorkspace).stream().findFirst()).isEmpty();
    }
}
