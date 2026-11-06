package pl.rozowi.app.dao;

import pl.rozowi.app.database.DatabaseManager;
import pl.rozowi.app.models.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing reports in the database.
 * Provides basic CRUD operations for report entities.
 */
public class ReportDAO {

    /**
     * Adds a new report to the database.
     *
     * @param report report object to save, containing all required data
     * @return true if the report was added successfully, false on error
     */
    public boolean insertReport(Report report) {
        String sql = "INSERT INTO reports (report_name, report_type, report_scope, created_by, created_at, exported_file) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getReportName());
            stmt.setString(2, report.getReportType());
            stmt.setString(3, report.getReportScope());
            stmt.setInt(4, report.getCreatedBy());
            stmt.setTimestamp(5, report.getCreatedAt());
            stmt.setString(6, report.getExportedFile());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the list of all reports from the database.
     *
     * @return list of all reports in the system, empty if there are none
     */
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Report report = new Report();
                report.setId(rs.getInt("id"));
                report.setReportName(rs.getString("report_name"));
                report.setReportType(rs.getString("report_type"));
                report.setReportScope(rs.getString("report_scope"));
                report.setCreatedBy(rs.getInt("created_by"));
                report.setCreatedAt(rs.getTimestamp("created_at"));
                report.setExportedFile(rs.getString("exported_file"));
                reports.add(report);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reports;
    }
}
