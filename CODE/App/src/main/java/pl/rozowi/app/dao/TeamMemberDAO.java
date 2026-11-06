package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class for team members.
 * Contains methods for managing user assignments to teams.
 */
public class TeamMemberDAO {

    /**
     * Gets the list of members of the given team.
     *
     * @param teamId team ID
     * @return list of User objects representing the team members
     */
    public List<User> getTeamMembers(int teamId) {
        String sql = """
                SELECT u.id, u.name, u.last_name, u.email
                FROM team_members tm
                JOIN users u ON u.id = tm.user_id
                WHERE tm.team_id = ?
                """;
        List<User> members = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setLastName(rs.getString("last_name"));
                u.setEmail(rs.getString("email"));
                members.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * Gets the ID of the team the user is assigned to.
     *
     * @param userId user ID
     * @return team ID, or 0 if the user is not assigned to any team
     */
    public int getTeamIdForUser(int userId) {
        String sql = "SELECT team_id FROM team_members WHERE user_id = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("team_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Checks whether the user is a team leader.
     *
     * @param teamId team ID
     * @param userId user ID
     * @return true if the user is the leader, false otherwise
     */
    public boolean isTeamLeader(int teamId, int userId) {
        String sql = "SELECT is_leader FROM team_members WHERE team_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_leader");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Adds a user to a team.
     *
     * @param teamId team ID
     * @param userId user ID
     * @param isLeader flag indicating whether the user is to be a leader
     * @return true if the operation succeeded, false otherwise
     * @throws SQLException on a database access error
     */
    public boolean insertTeamMember(int teamId, int userId, boolean isLeader) throws SQLException {
        String sql = "INSERT INTO team_members (team_id, user_id, is_leader) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, userId);
            stmt.setBoolean(3, isLeader);
            int affected = stmt.executeUpdate();
            return affected > 0;
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, userId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a user's team assignment.
     * If the user is already assigned to another team, the old assignment is removed.
     *
     * @param userId user ID
     * @param newTeamId ID of the new team
     * @return true if the operation succeeded, false otherwise
     */
    public boolean updateUserTeam(int userId, int newTeamId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int currentTeamId = getTeamIdForUser(userId);

                if (currentTeamId == newTeamId) {
                    return true;
                }

                if (currentTeamId > 0) {
                    try (PreparedStatement deleteStmt = conn.prepareStatement(
                            "DELETE FROM team_members WHERE user_id = ?")) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.executeUpdate();
                    }
                }

                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO team_members (team_id, user_id, is_leader) VALUES (?, ?, ?)")) {
                    insertStmt.setInt(1, newTeamId);
                    insertStmt.setInt(2, userId);
                    insertStmt.setBoolean(3, false);
                    insertStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the list of IDs of all teams the user belongs to.
     *
     * @param userId user ID
     * @return list of team IDs
     */
    public List<Integer> getAllTeamIdsForUser(int userId) {
        List<Integer> teamIds = new ArrayList<>();
        String sql = "SELECT team_id FROM team_members WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teamIds.add(rs.getInt("team_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return teamIds;
    }

    /**
     * Gets the list of IDs of teams in which the user is the leader.
     *
     * @param userId user ID
     * @return list of team IDs
     */
    public List<Integer> getTeamIdsForTeamLeader(int userId) {
        List<Integer> teamIds = new ArrayList<>();
        String sql = "SELECT team_id FROM team_members WHERE user_id = ? AND is_leader = true";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teamIds.add(rs.getInt("team_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return teamIds;
    }
}