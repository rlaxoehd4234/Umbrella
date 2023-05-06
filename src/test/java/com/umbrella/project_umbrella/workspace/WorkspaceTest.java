package com.umbrella.project_umbrella.workspace;

import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class WorkspaceTest {
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
    @DisplayName("[SUCCESS]_Add_One_User_In_One_Workspace")
    public void AddOneUserInOneWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);
        User theUser = createUser(0);
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);

        assertThat(theWorkspaceUser.getWorkspaceUser().getEmail()).isEqualTo(theUser.getEmail());
    }

    @Test
    @DisplayName("[SUCCESS]_Add_Two_Users_In_One_Workspace")
    public void AddTwoUsersInOneWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);

        User theUser0 = createUser(0);
        User theUser1 = createUser(1);

        WorkspaceUser theWorkspaceUser0 = new WorkspaceUser();
        WorkspaceUser theWorkspaceUser1= new WorkspaceUser();

        theUser0.enterWorkspaceUser(theWorkspaceUser0);
        theWorkspaceUser0.takeWorkspace(theWorkspace);

        theUser1.enterWorkspaceUser(theWorkspaceUser1);
        theWorkspaceUser1.takeWorkspace(theWorkspace);

        theWorkspace.getWorkspaceUsers().stream().forEach(
                user -> System.out.println(user.getWorkspaceUser().getEmail())
        );

        assertThat(theWorkspace.getWorkspaceUsers().size()).isEqualTo(2);
        assertThat(theWorkspace.getWorkspaceUsers().get(0).getWorkspaceUser().getEmail()).isEqualTo(theUser0.getEmail());
        assertThat(theWorkspace.getWorkspaceUsers().get(1).getWorkspaceUser().getEmail()).isEqualTo(theUser1.getEmail());
    }

    @Test
    @DisplayName("[SUCCESS]_Add_One_User_In_Two_Workspaces")
    public void AddOneUserInTwoWorkspaces() {
        WorkSpace theWorkspace0 = createWorkspace(0);
        WorkSpace theWorkspace1 = createWorkspace(1);

        User theUser = createUser(0);

        WorkspaceUser theWorkspaceUser0 = new WorkspaceUser();
        WorkspaceUser theWorkspaceUser1 = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser0);
        theWorkspaceUser0.takeWorkspace(theWorkspace0);
        theUser.enterWorkspaceUser(theWorkspaceUser1);
        theWorkspaceUser1.takeWorkspace(theWorkspace1);

        theUser.getWorkspaceUsers().stream().forEach(
                workspace -> System.out.println(workspace.getWorkspace().getTitle())
        );

        assertThat(theUser.getWorkspaceUsers().size()).isEqualTo(2);
        assertThat(theUser.getWorkspaceUsers().get(0).getWorkspace().getTitle()).isEqualTo(theWorkspace0.getTitle());
        assertThat(theUser.getWorkspaceUsers().get(1).getWorkspace().getTitle()).isEqualTo(theWorkspace1.getTitle());
    }

    @Test
    @DisplayName("[SUCCESS]_Out_One_User_In_One_Workspace")
    public void OutOneUserInOneWorkspaceTest() {
        WorkSpace theWorkspace = createWorkspace(0);
        User theUser = createUser(0);
        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);

        theUser.exitWorkspaceUser(theWorkspaceUser);

        assertThat(theUser.getWorkspaceUsers().size()).isEqualTo(0);
        assertThat(theWorkspace.getWorkspaceUsers().size()).isEqualTo(0);
    }
}
