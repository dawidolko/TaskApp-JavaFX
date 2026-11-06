package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing user roles in the system.
 * Provides basic CRUD operations on the roles table in the database.
 */
public class RoleDAO {

    /**
     * Gets all roles from the database.
     *
     * @return list of {@link Role} objects representing all roles in the system.
     *         Empty list if no roles were found.
     */
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT id, role_name, permissions FROM roles";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleName(rs.getString("role_name"));
                role.setPermissions(rs.getString("permissions"));
                roles.add(role);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return roles;
    }

    /**
     * Finds a role by name.
     *
     * @param roleName name of the role to find
     * @return the {@link Role} object if found, null otherwise
     */
    public Role getRoleByName(String roleName) {
        Role role = null;
        String sql = "SELECT * FROM roles WHERE role_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleName(rs.getString("role_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return role;
    }

    /**
     * Adds a new role to the database.
     *
     * @param role {@link Role} object containing the new role's name
     * @return true if the role was added successfully, false on error
     */
    public boolean insertRole(Role role) {
        String sql = "INSERT INTO roles (role_name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}