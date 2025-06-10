# TaskApp-JavaFX - Task Management System

**TaskApp-JavaFX** is a comprehensive task management system designed for IT companies, built with JavaFX. The system supports different user roles (Administrator, Manager, User) and provides comprehensive project management, task tracking, and reporting capabilities. This project is developed as part of the Team Programming course.

## 📁 Project Structure

### Main Directories Overview

```
TASKAPP-JAVAFX/
├── 🖥️ CODE/           # Application source code
├── 📊 DAILY/           # Daily progress reports
├── 📚 DOC/             # Documentation and images
├── 🎨 GUI/IMG/             # GUI mockups and designs
├── 🔧 INSTALL/         # Installation files
├── 📖 JAVA_DOCS/       # Java documentation
├── 📄 PDF/             # PDF documentation
├── 📊 SLIDES/          # Presentation slides
├── 🗄️ SQL/             # Database scripts and ERD
├── 📋 UML/             # UML diagrams
├── 📜 LICENSE          # Project license
└── 📝 README.md        # Project documentation
```

### Detailed Directory Contents

#### 🎨 GUI Directory

Contains all user interface screenshots and mockups:

- **AddingEmployee.png** - Employee addition interface
- **AddingTasks.png** - Task creation interface
- **AdminEditTeam.png** - Team editing for administrators
- **AdminLogs.png** - System logs view
- **AdminReports.png** - Administrative reporting
- **AdminSettings.png** - System configuration
- **AdminTeamCreation.png** - Team creation interface
- **AdminTeams.png** - Team management overview
- **DashboardAdmin.png** - Administrator dashboard
- **EditEmployee.png** - Employee editing interface
- **EditTask.png** - Task modification interface
- **EmployeeAdmin.png** - Employee management
- **EmployeesAdmin.png** - Employee overview
- **Login.png** - Login screen
- **ManagerDashboard.png** - Manager dashboard
- **ManagerEmployees.png** - Manager's employee view
- **ManagerReports.png** - Manager reporting
- **ManagerSettings.png** - Manager configuration
- **Registration.png** - User registration
- **SettingsManager.png** - Settings management
- **UserDashboard.png** - User dashboard
- **UserEditData.png** - User profile editing
- **UserSettings.png** - User preferences

#### 📋 UML Directory

Contains system design diagrams:

- **ChartActivities1-7.png** - Activity diagrams (7 charts)
- **SequenceDiagram3-6.png** - Sequence diagrams (4 diagrams)
- **UseCaseDiagram.png** - Use case diagram

#### 🗄️ SQL Directory

Database-related files:

- **erd1.png, erd2.png** - Entity Relationship Diagrams
- **migration.sql** - Database migration scripts
- **seeder.sql** - Database seeding data

## Key Features

🔐 **User & Role Management**  
Create, edit, and delete user accounts with role-based access control across three user types

📋 **Task Management**  
Create, assign, and track tasks with progress monitoring for development teams

📊 **Reporting Module**  
Generate detailed PDF reports with filtering options (status, priority, assignee, etc.)

⚙️ **System Configuration**  
Customize system settings, define new roles, priorities, and task statuses

🗄️ **Database Integration**  
Robust data persistence for users, projects, tasks, and generated reports

👥 **Team Management**  
Comprehensive team creation, editing, and management capabilities

📈 **Activity Tracking**  
Monitor user activities and system logs for better oversight

## Architecture

The project follows a **layered architecture**:

- **Frontend (JavaFX)** - User interface with intuitive GUI and animations
- **Backend (Java)** - Business logic and database communication
- **Database Layer** - Data storage for users, tasks, projects, and reports

## Screenshots

### Authentication & Registration

![Login Screen](GUI/IMG/Login.png)
_Login interface for system access_

![Registration Screen](GUI/IMG/Registration.png)
_User registration interface_

### Administrator Views

![Admin Dashboard](GUI/IMG/DashboardAdmin.png)
_Administrator Dashboard - Complete system overview_

![Admin Teams](GUI/IMG/AdminTeams.png)
_Team management interface_

![Admin Team Creation](GUI/IMG/AdminTeamCreation.png)
_Team creation interface_

![Admin Edit Team](GUI/IMG/AdminEditTeam.png)
_Team editing capabilities_

![Employee Management](GUI/IMG/EmployeeAdmin.png)
_Employee administration panel_

![Adding Employee](GUI/IMG/AddingEmployee.png)
_Employee addition interface_

![Edit Employee](GUI/IMG/EditEmployee.png)
_Employee editing interface_

![Admin Settings](GUI/IMG/AdminSettings.png)
_System configuration settings_

![Admin Logs](GUI/IMG/AdminLogs.png)
_System activity logs_

### Manager Views

![Manager Dashboard](GUI/IMG/ManagerDashboard.png)
_Manager Dashboard - Team and project management_

![Manager Employees](GUI/IMG/ManagerEmployees.png)
_Manager's employee overview_

![Manager Settings](GUI/IMG/ManagerSettings.png)
_Manager configuration options_

### User Views

![User Edit Data](GUI/IMG/UserEditData.png)
_User profile editing_

![User Settings](GUI/IMG/UserSettings.png)
_User preference settings_

### Task Management

![Adding Tasks](GUI/IMG/AddingTasks.png)
_Task creation interface_

![Edit Task](GUI/IMG/EditTask.png)
_Task modification interface_

## System Design

### Use Case Diagram

![Use Case Diagram](UML/UseCaseDiagram.png)
_Complete system use cases and user interactions_

### Activity Diagrams

![Activity Chart 1](UML/ChartActivities1.png)
_User authentication flow_

![Activity Chart 2](UML/ChartActivities2.png)
_Task management processes_

![Activity Chart 3](UML/ChartActivities3.png)
_Employee management workflow_

![Activity Chart 4](UML/ChartActivities4.png)
_Reporting generation process_

![Activity Chart 5](UML/ChartActivities5.png)
_Team management activities_

![Activity Chart 6](UML/ChartActivities6.png)
_System configuration flow_

![Activity Chart 7](UML/ChartActivities7.png)
_Administrative operations_

### Sequence Diagrams

![Sequence Diagram 3](UML/SequenceDiagram3.png)
_System interaction sequences_

![Sequence Diagram 4](UML/SequenceDiagram4.png)
_Database communication flow_

![Sequence Diagram 5](UML/SequenceDiagram5.png)
_User role interactions_

![Sequence Diagram 6](UML/SequenceDiagram6.png)
_Task processing sequence_

## Database Design

### Entity Relationship Diagrams

![ERD 1](SQL/erd1.png)
_Main database structure and relationships_

![ERD 2](SQL/erd2.png)
_Detailed entity relationships_

## Team Roles

- **Dawid** - Frontend development (JavaFX, UI, animations)
- **Piotr** - Backend development (business logic, database integration)
- **Łukasz** - Database design and implementation
- **Piotr** - Database support (queries, migrations, optimization)

## Tech Stack

- **Java** with JavaFX for desktop application
- **Maven** for project management
- **FXML** for UI layout
- **SQL Database** with migration and seeding support
- **UML** for system design documentation

## Getting Started

1. Clone the repository
2. Navigate to the CODE directory
3. Build with Maven: `mvn clean install`
4. Set up database using SQL scripts in SQL directory
5. Run: `mvn javafx:run` or execute `MainApplication` in your IDE

## Documentation

- **📊 SLIDES/** - Project presentations
- **📄 PDF/** - Detailed documentation in PDF format
- **📖 JAVA_DOCS/** - Java API documentation
- **📚 DOC/** - Additional project documentation

## Project Status

🚧 **In Development** - Part of Team Programming coursework  
All major modules implemented with comprehensive UI and database integration.
