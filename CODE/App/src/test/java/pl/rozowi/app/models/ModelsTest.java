package pl.rozowi.app.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Test class verifying the application's data models.
 */
public class ModelsTest {

    /**
     * Tests the getters and setters of all User properties.
     * Verifies that all fields are set and read correctly.
     */
    @Test
    public void testUserProperties() {
        User user = new User();

        user.setId(1);
        user.setName("John");
        user.setLastName("Doe");
        user.setPassword("securePassword123!");
        user.setEmail("john.doe@example.com");
        user.setRoleId(2);
        user.setGroupId(3);
        user.setPasswordHint("First pet name");
        user.setTheme("Dark");
        user.setDefaultView("Dashboard");
        user.setTeamName("Development");

        assertEquals(1, user.getId());
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getLastName());
        assertEquals("securePassword123!", user.getPassword());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals(2, user.getRoleId());
        assertEquals(3, user.getGroupId());
        assertEquals("First pet name", user.getPasswordHint());
        assertEquals("Dark", user.getTheme());
        assertEquals("Dashboard", user.getDefaultView());
        assertEquals("Development", user.getTeamName());
    }
}

/**
 * Test suite for the Team class.
 * Verifies basic properties and JavaFX mechanisms.
 */
class TeamTest {

    /**
     * Tests the basic getters and setters of the Team class.
     * Verifies that the id, teamName and projectId fields are set and read
     * correctly.
     */
    @Test
    public void testTeamProperties() {
        Team team = new Team();

        team.setId(5);
        team.setTeamName("Quality Assurance");
        team.setProjectId(10);

        assertEquals(5, team.getId());
        assertEquals("Quality Assurance", team.getTeamName());
        assertEquals(10, team.getProjectId());
    }

    /**
     * Tests the JavaFX properties of the Team class.
     * Verifies that the JavaFX properties correctly reflect the field values.
     */
    @Test
    public void testTeamJavaFXProperties() {
        Team team = new Team();

        team.setId(7);
        team.setTeamName("Backend");
        team.setProjectId(15);

        assertEquals(7, team.idProperty().get());
        assertEquals("Backend", team.teamNameProperty().get());
        assertEquals(15, team.projectIdProperty().get());
    }

    /**
     * Tests the toString() implementation of the Team class.
     * The method should return a text representation of the object in the format
     * "id - teamName", with an en dash as the separator.
     */
    @Test
    public void testToString() {
        Team team = new Team();

        team.setId(8);
        team.setTeamName("Frontend");

        assertEquals("8 – Frontend", team.toString());
    }
}

/**
 * Test suite for the Project class.
 * Verifies basic properties, JavaFX mechanisms and method aliases.
 */
class ProjectTest {

    /**
     * Tests the basic getters and setters of the Project class.
     * Verifies that all project fields are set and read correctly.
     */
    @Test
    public void testProjectProperties() {
        Project project = new Project();
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 31);

        project.setId(10);
        project.setProjectName("Mobile App");
        project.setDescription("Develop a new mobile application");
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setManagerId(5);

        assertEquals(10, project.getId());
        assertEquals("Mobile App", project.getProjectName());
        assertEquals("Develop a new mobile application", project.getDescription());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
        assertEquals(5, project.getManagerId());
    }

    /**
     * Tests the JavaFX properties of the Project class.
     * Verifies that the JavaFX properties correctly reflect the field values.
     */
    @Test
    public void testProjectJavaFXProperties() {
        Project project = new Project();
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        project.setId(15);
        project.setProjectName("Web Platform");
        project.setDescription("Create a new web platform");
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setManagerId(8);

        assertEquals(15, project.idProperty().get());
        assertEquals("Web Platform", project.projectNameProperty().get());
        assertEquals("Create a new web platform", project.descriptionProperty().get());
        assertEquals(startDate, project.startDateProperty().get());
        assertEquals(endDate, project.endDateProperty().get());
        assertEquals(8, project.managerIdProperty().get());
    }

    /**
     * Tests the getName() and setName() aliases for getProjectName() and
     * setProjectName().
     * Verifies that the alias correctly sets and reads the projectName field.
     */
    @Test
    public void testNameAlias() {
        Project project = new Project();

        project.setProjectName("API Project");
        assertEquals("API Project", project.getName());

        project.setName("Updated API Project");
        assertEquals("Updated API Project", project.getProjectName());
        assertEquals("Updated API Project", project.getName());
    }

    /**
     * Tests the toString() implementation of the Project class.
     * The method should return a text representation of the object in the format
     * "id - projectName".
     */
    @Test
    public void testToString() {
        Project project = new Project();

        project.setId(20);
        project.setProjectName("Database Migration");

        assertEquals("20 - Database Migration", project.toString());
    }
}

/**
 * Test suite for the Task class.
 * Verifies basic properties and JavaFX mechanisms.
 */
class TaskTest {

    /**
     * Tests the getters and setters of all Task properties.
     * Verifies that all task fields are set and read correctly.
     */
    @Test
    public void testTaskProperties() {
        Task task = new Task();

        task.setId(25);
        task.setProjectId(10);
        task.setTeamId(5);
        task.setAssignedTo(15);
        task.setTitle("Implement login functionality");
        task.setDescription("Create secure login forms and authentication");
        task.setStatus("In Progress");
        task.setPriority("High");
        task.setStartDate("2025-05-20");
        task.setEndDate("2025-06-10");
        task.setTeamName("Frontend Team");
        task.setAssignedEmail("developer@example.com");

        assertEquals(25, task.getId());
        assertEquals(10, task.getProjectId());
        assertEquals(5, task.getTeamId());
        assertEquals(15, task.getAssignedTo());
        assertEquals("Implement login functionality", task.getTitle());
        assertEquals("Create secure login forms and authentication", task.getDescription());
        assertEquals("In Progress", task.getStatus());
        assertEquals("High", task.getPriority());
        assertEquals("2025-05-20", task.getStartDate());
        assertEquals("2025-06-10", task.getEndDate());
        assertEquals("Frontend Team", task.getTeamName());
        assertEquals("developer@example.com", task.getAssignedEmail());
    }

    /**
     * Tests the JavaFX properties of the Task class.
     * Verifies that the JavaFX properties correctly reflect the field values.
     */
    @Test
    public void testTaskJavaFXProperties() {
        Task task = new Task();

        task.setId(30);
        task.setTitle("Fix layout issues");
        task.setStatus("New");

        assertEquals(30, task.idProperty().get());
        assertEquals("Fix layout issues", task.titleProperty().get());
        assertEquals("New", task.statusProperty().get());
    }

    /**
     * Tests the toString() implementation of the Task class.
     * The method should return a text representation of the object in the format
     * "id - title", with an en dash as the separator.
     */
    @Test
    public void testToString() {
        Task task = new Task();

        task.setId(35);
        task.setTitle("Optimize database queries");

        assertEquals("35 – Optimize database queries", task.toString());
    }
}

/**
 * Test suite for the TeamMember class.
 * Verifies the getters and setters of the class properties.
 */
class TeamMemberTest {

    /**
     * Tests the getters and setters of all TeamMember properties.
     * Verifies that the teamId, userId and leader fields are set and read
     * correctly.
     */
    @Test
    public void testTeamMemberProperties() {
        TeamMember teamMember = new TeamMember();

        teamMember.setTeamId(10);
        teamMember.setUserId(5);
        teamMember.setLeader(true);

        assertEquals(10, teamMember.getTeamId());
        assertEquals(5, teamMember.getUserId());
        assertTrue(teamMember.isLeader());

        teamMember.setLeader(false);
        assertFalse(teamMember.isLeader());
    }
}

/**
 * Test suite for the TaskActivity class.
 * Verifies the getters and setters and the toString() method.
 */
class TaskActivityTest {

    /**
     * Tests the getters and setters of all TaskActivity properties.
     * Verifies that all fields are set and read correctly.
     */
    @Test
    public void testTaskActivityProperties() {
        TaskActivity activity = new TaskActivity();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        activity.setId(15);
        activity.setTaskId(25);
        activity.setUserId(10);
        activity.setActivityType("UPDATE");
        activity.setDescription("Updated task priority from Medium to High");
        activity.setActivityDate(timestamp);

        assertEquals(15, activity.getId());
        assertEquals(25, activity.getTaskId());
        assertEquals(10, activity.getUserId());
        assertEquals("UPDATE", activity.getActivityType());
        assertEquals("Updated task priority from Medium to High", activity.getDescription());
        assertEquals(timestamp, activity.getActivityDate());
    }

    /**
     * Tests the toString() implementation of the TaskActivity class.
     * The method should return a text representation of the object containing all
     * fields.
     */
    @Test
    public void testToString() {
        TaskActivity activity = new TaskActivity();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        activity.setId(20);
        activity.setTaskId(30);
        activity.setUserId(15);
        activity.setActivityType("STATUS");
        activity.setDescription("Changed status from New to In Progress");
        activity.setActivityDate(timestamp);

        String expected = "TaskActivity{" +
                "id=" + 20 +
                ", taskId=" + 30 +
                ", userId=" + 15 +
                ", activityType='" + "STATUS" + '\'' +
                ", description='" + "Changed status from New to In Progress" + '\'' +
                ", activityDate=" + timestamp +
                '}';
        assertEquals(expected, activity.toString());
    }
}

/**
 * Test suite for the EnhancedTaskActivity class.
 * Verifies inheritance from TaskActivity, the additional fields and the helper
 * methods.
 */
class EnhancedTaskActivityTest {

    /**
     * Tests the constructor and getters/setters of EnhancedTaskActivity.
     * Verifies both the fields inherited from TaskActivity and the additional
     * fields specific to EnhancedTaskActivity.
     */
    @Test
    public void testEnhancedTaskActivityProperties() {
        TaskActivity baseActivity = new TaskActivity();
        baseActivity.setId(25);
        baseActivity.setTaskId(40);
        baseActivity.setUserId(20);
        baseActivity.setActivityType("COMMENT");
        baseActivity.setDescription("Added comment: Need more details");
        baseActivity.setActivityDate(new Timestamp(System.currentTimeMillis()));

        EnhancedTaskActivity enhancedActivity = new EnhancedTaskActivity(baseActivity);
        enhancedActivity.setTaskTitle("Implement search functionality");
        enhancedActivity.setUserName("John");
        enhancedActivity.setUserLastName("Doe");
        enhancedActivity.setUserEmail("john.doe@example.com");

        assertEquals(25, enhancedActivity.getId());
        assertEquals(40, enhancedActivity.getTaskId());
        assertEquals(20, enhancedActivity.getUserId());
        assertEquals("COMMENT", enhancedActivity.getActivityType());
        assertEquals("Added comment: Need more details", enhancedActivity.getDescription());
        assertNotNull(enhancedActivity.getActivityDate());
        assertEquals("Implement search functionality", enhancedActivity.getTaskTitle());
        assertEquals("John", enhancedActivity.getUserName());
        assertEquals("Doe", enhancedActivity.getUserLastName());
        assertEquals("john.doe@example.com", enhancedActivity.getUserEmail());
    }

    /**
     * Tests getFullUserName() in different scenarios:
     * - when both first and last name are set
     * - when only the first name is set
     * - when only the last name is set
     * - when neither first nor last name is set
     */
    @Test
    public void testGetFullUserName() {
        EnhancedTaskActivity activity = new EnhancedTaskActivity();

        activity.setUserName("Jane");
        activity.setUserLastName("Smith");
        assertEquals("Jane Smith", activity.getFullUserName());

        activity = new EnhancedTaskActivity();
        activity.setUserName("Jane");
        assertEquals("Jane", activity.getFullUserName());

        activity = new EnhancedTaskActivity();
        activity.setUserLastName("Smith");
        assertEquals("Smith", activity.getFullUserName());

        activity = new EnhancedTaskActivity();
        activity.setUserId(30);
        assertEquals("Nieznany użytkownik (ID: 30)", activity.getFullUserName());
    }

    /**
     * Tests getUserDisplayString() in different scenarios:
     * - when first name, last name and email are set
     * - when only first and last name are set
     */
    @Test
    public void testGetUserDisplayString() {
        EnhancedTaskActivity activity = new EnhancedTaskActivity();

        activity.setUserName("Robert");
        activity.setUserLastName("Johnson");
        activity.setUserEmail("robert.j@example.com");
        assertEquals("Robert Johnson (robert.j@example.com)", activity.getUserDisplayString());

        activity = new EnhancedTaskActivity();
        activity.setUserName("Robert");
        activity.setUserLastName("Johnson");
        assertEquals("Robert Johnson", activity.getUserDisplayString());
    }

    /**
     * Tests getTaskTitleOrDefault() in different scenarios:
     * - when taskId equals 0
     * - when the task title is set
     * - when the task title is not set
     */
    @Test
    public void testGetTaskTitleOrDefault() {
        EnhancedTaskActivity activity = new EnhancedTaskActivity();

        activity.setTaskId(0);
        assertEquals("Zdarzenie systemowe", activity.getTaskTitleOrDefault());

        activity = new EnhancedTaskActivity();
        activity.setTaskId(50);
        activity.setTaskTitle("Create user registration form");
        assertEquals("Create user registration form", activity.getTaskTitleOrDefault());

        activity = new EnhancedTaskActivity();
        activity.setTaskId(60);
        assertEquals("Zadanie ID: 60", activity.getTaskTitleOrDefault());
    }

    /**
     * Tests getActivityTypeDisplayName(), which maps technical activity type names
     * to their user-friendly equivalents.
     * Verifies the mapping for all supported types and for special cases.
     */
    @Test
    public void testGetActivityTypeDisplayName() {
        EnhancedTaskActivity activity = new EnhancedTaskActivity();

        activity.setActivityType("CREATE");
        assertEquals("Utworzenie", activity.getActivityTypeDisplayName());

        activity.setActivityType("UPDATE");
        assertEquals("Aktualizacja", activity.getActivityTypeDisplayName());

        activity.setActivityType("STATUS");
        assertEquals("Zmiana statusu", activity.getActivityTypeDisplayName());

        activity.setActivityType("ASSIGN");
        assertEquals("Przypisanie", activity.getActivityTypeDisplayName());

        activity.setActivityType("COMMENT");
        assertEquals("Komentarz", activity.getActivityTypeDisplayName());

        activity.setActivityType("PASSWORD");
        assertEquals("Zmiana hasła", activity.getActivityTypeDisplayName());

        activity.setActivityType("LOGIN");
        assertEquals("Logowanie", activity.getActivityTypeDisplayName());

        activity.setActivityType("LOGOUT");
        assertEquals("Wylogowanie", activity.getActivityTypeDisplayName());

        activity.setActivityType("USER_MANAGEMENT");
        assertEquals("Zarządzanie użytkownikami", activity.getActivityTypeDisplayName());

        activity.setActivityType("TEAM_MANAGEMENT");
        assertEquals("Zarządzanie zespołami", activity.getActivityTypeDisplayName());

        activity.setActivityType("CUSTOM_TYPE");
        assertEquals("CUSTOM_TYPE", activity.getActivityTypeDisplayName());

        activity.setActivityType(null);
        assertEquals("Nieznany", activity.getActivityTypeDisplayName());
    }

    /**
     * Tests getActivityCssClasses(), which returns the CSS classes appropriate for
     * the given activity type. Verifies the mapping for different types and for
     * custom and undefined cases.
     */
    @Test
    public void testGetActivityCssClasses() {
        EnhancedTaskActivity activity = new EnhancedTaskActivity();

        activity.setActivityType("CREATE");
        assertEquals("activity-create", activity.getActivityCssClasses());

        activity.setActivityType("UPDATE");
        assertEquals("activity-update", activity.getActivityCssClasses());

        activity.setActivityType("STATUS");
        assertEquals("activity-status", activity.getActivityCssClasses());

        activity.setActivityType("CUSTOM_TYPE");
        assertEquals("activity-other", activity.getActivityCssClasses());

        activity.setActivityType(null);
        assertEquals("activity-unknown", activity.getActivityCssClasses());
    }
}

/**
 * Test suite for the Role class.
 * Verifies the getters and setters of the class properties.
 */
class RoleTest {

    /**
     * Tests the getters and setters of all Role properties.
     * Verifies that the id, roleName and permissions fields are set and read
     * correctly.
     */
    @Test
    public void testRoleProperties() {
        Role role = new Role();

        role.setId(2);
        role.setRoleName("Manager");
        role.setPermissions("VIEW_USERS,EDIT_TASKS,VIEW_PROJECTS");

        assertEquals(2, role.getId());
        assertEquals("Manager", role.getRoleName());
        assertEquals("VIEW_USERS,EDIT_TASKS,VIEW_PROJECTS", role.getPermissions());
    }
}

/**
 * Test suite for the Settings class.
 * Verifies the getters and setters of the class properties.
 */
class SettingsTest {

    /**
     * Tests the getters and setters of all Settings properties.
     * Verifies that the id, userId, theme, defaultView and lastPasswordChange
     * fields are set and read correctly.
     */
    @Test
    public void testSettingsProperties() {
        Settings settings = new Settings();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        settings.setId(5);
        settings.setUserId(10);
        settings.setTheme("Dark");
        settings.setDefaultView("Dashboard");
        settings.setLastPasswordChange(timestamp);

        assertEquals(5, settings.getId());
        assertEquals(10, settings.getUserId());
        assertEquals("Dark", settings.getTheme());
        assertEquals("Dashboard", settings.getDefaultView());
        assertEquals(timestamp, settings.getLastPasswordChange());
    }
}

/**
 * Test suite for the Report class.
 * Verifies the getters and setters of the class properties.
 */
class ReportTest {

    /**
     * Tests the getters and setters of all Report properties.
     * Verifies that all report fields are set and read correctly.
     */
    @Test
    public void testReportProperties() {
        Report report = new Report();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        report.setId(15);
        report.setReportName("Monthly Tasks Summary");
        report.setReportType("TASKS");
        report.setReportScope("TEAM");
        report.setCreatedBy(20);
        report.setCreatedAt(timestamp);
        report.setExportedFile("tasks_summary_2025_05.pdf");

        assertEquals(15, report.getId());
        assertEquals("Monthly Tasks Summary", report.getReportName());
        assertEquals("TASKS", report.getReportType());
        assertEquals("TEAM", report.getReportScope());
        assertEquals(20, report.getCreatedBy());
        assertEquals(timestamp, report.getCreatedAt());
        assertEquals("tasks_summary_2025_05.pdf", report.getExportedFile());
    }
}

/**
 * Test suite for the TaskAssignment class.
 * Verifies the getters and setters of the class properties.
 */
class TaskAssignmentTest {

    /**
     * Tests the getters and setters of all TaskAssignment properties.
     * Verifies that the taskId and userId fields are set and read correctly.
     */
    @Test
    public void testTaskAssignmentProperties() {
        TaskAssignment assignment = new TaskAssignment();

        assignment.setTaskId(25);
        assignment.setUserId(10);

        assertEquals(25, assignment.getTaskId());
        assertEquals(10, assignment.getUserId());
    }
}