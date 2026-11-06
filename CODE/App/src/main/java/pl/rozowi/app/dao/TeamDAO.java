package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Team;
import pl.rozowi.app.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class for teams.
 * Contains methods for managing teams, their members and relations in the system.
 */
public class TeamDAO {

    /**
     * Gets the list of all teams from the database.
     *
     * @return list of all teams
     * @throws SQLException on an error communicating with the database
     */
    public List<Team> getAllTeams() throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT id, team_name, project_id FROM teams";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                Team t = new Team();
                t.setId(rs.getInt("id"));
                t.setTeamName(rs.getString("team_name"));
                t.setProjectId(rs.getInt("project_id"));
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Adds a new team to the database.
     *
     * @param t Team object containing the new team's data
     * @return true if the operation succeeded, false otherwise
     */
    public boolean insertTeam(Team t) {
        String sql = "INSERT INTO teams (team_name, project_id) VALUES (?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, t.getTeamName());
            s.setInt(2, t.getProjectId());
            if (s.executeUpdate() == 0) return false;
            ResultSet keys = s.getGeneratedKeys();
            if (keys.next()) t.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a team's data in the database.
     *
     * @param t Team object containing the updated data
     * @return true if the operation succeeded, false otherwise
     * @throws SQLException on an error communicating with the database
     */
    public boolean updateTeam(Team t) throws SQLException {
        String sql = "UPDATE teams SET team_name = ?, project_id = ? WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, t.getTeamName());
            s.setInt(2, t.getProjectId());
            s.setInt(3, t.getId());
            return s.executeUpdate() > 0;
        }
    }

    /**
     * Gets the list of members of the given team.
     *
     * @param teamId team ID
     * @return list of users who are members of the team
     */
    public List<User> getTeamMembers(int teamId) {
        List<User> members = new ArrayList<>();
        String sql = """
                    SELECT u.id, u.name, u.last_name, u.email, u.role_id, tm.is_leader
                    FROM users u
                    JOIN team_members tm ON u.id = tm.user_id
                    WHERE tm.team_id = ?
                """;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, teamId);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setLastName(rs.getString("last_name"));
                u.setEmail(rs.getString("email"));
                u.setRoleId(rs.getInt("role_id"));
                members.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return members;
    }

    /**
     * Adds a user to a team.
     *
     * @param teamId team ID
     * @param userId user ID
     * @param isLeader flag indicating whether the user is the team leader
     * @return true if the operation succeeded, false otherwise
     * @throws SQLException on an error communicating with the database
     */
    public boolean insertTeamMember(int teamId, int userId, boolean isLeader) throws SQLException {
        String sql = "INSERT INTO team_members (team_id, user_id, is_leader) VALUES (?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, teamId);
            s.setInt(2, userId);
            s.setBoolean(3, isLeader);
            return s.executeUpdate() > 0;
        }
    }

    /**
     * Removes a user from a team.
     *
     * @param teamId team ID
     * @param userId user ID
     * @return true if the operation succeeded, false otherwise
     */
    public boolean deleteTeamMember(int teamId, int userId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, teamId);
            s.setInt(2, userId);
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the list of teams the given user belongs to.
     *
     * @param userId user ID
     * @return list of the user's teams
     */
    public List<Team> getTeamsForUser(int userId) {
        List<Team> teams = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.team_name, t.project_id
                      FROM teams t
                      JOIN team_members tm ON t.id = tm.team_id
                     WHERE tm.user_id = ?
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team t = new Team();
                    t.setId(rs.getInt("id"));
                    t.setTeamName(rs.getString("team_name"));
                    t.setProjectId(rs.getInt("project_id"));
                    teams.add(t);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return teams;
    }

    /**
     * Gets a team's name by its ID.
     *
     * @param teamId team ID
     * @return team name, or "-" if not found
     */
    public String getTeamNameById(int teamId) {
        String sql = "SELECT team_name FROM teams WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("team_name");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "–";
    }

    /**
     * Gets the list of teams managed by the given manager.
     *
     * @param managerId manager ID
     * @return list of teams managed by the manager
     * @throws SQLException on an error communicating with the database
     */
    public List<Team> getTeamsForManager(int managerId) throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.* FROM teams t " +
                "JOIN projects p ON t.project_id = p.id " +
                "WHERE p.manager_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team();
                    team.setId(rs.getInt("id"));
                    team.setTeamName(rs.getString("team_name"));
                    team.setProjectId(rs.getInt("project_id"));
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    /**
     * Gets a team by its ID.
     *
     * @param teamId team ID
     * @return Team object, or null if not found
     * @throws SQLException on an error communicating with the database
     */
    public Team getTeamById(int teamId) throws SQLException {
        String sql = "SELECT id, team_name, project_id FROM teams WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Team team = new Team();
                team.setId(rs.getInt("id"));
                team.setTeamName(rs.getString("team_name"));
                team.setProjectId(rs.getInt("project_id"));
                return team;
            }
        }
        return null;
    }

    /**
     * Gets the list of IDs of teams in which the given user is the leader.
     *
     * @param leaderId ID of the user who is the leader
     * @return list of team IDs
     * @throws SQLException on an error communicating with the database
     */
    public List<Integer> getTeamsByLeaderId(int leaderId) throws SQLException {
        List<Integer> teamIds = new ArrayList<>();
        String sql = "SELECT t.id FROM teams t " +
                "JOIN team_members tm ON t.id = tm.team_id " +
                "WHERE tm.user_id = ? AND tm.is_leader = true";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                teamIds.add(rs.getInt("id"));
            }
        }

        return teamIds;
    }

    /**
     * Gets the list of teams in which the given user is the leader.
     *
     * @param leaderId ID of the user who is the leader
     * @return list of Team objects
     * @throws SQLException on an error communicating with the database
     */
    public List<Team> getTeamsByLeaderIdAsList(int leaderId) throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.id, t.team_name, t.project_id FROM teams t " +
                "JOIN team_members tm ON t.id = tm.team_id " +
                "WHERE tm.user_id = ? AND tm.is_leader = true";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Team team = new Team();
                team.setId(rs.getInt("id"));
                team.setTeamName(rs.getString("team_name"));
                team.setProjectId(rs.getInt("project_id"));
                teams.add(team);
            }
        }

        return teams;
    }
}