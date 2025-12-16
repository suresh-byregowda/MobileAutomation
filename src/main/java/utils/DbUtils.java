
package utils;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Reusable JDBC helper for common DB operations.
 * Priority: JVM System props -> ConfigReader -> defaults.

 * Runtime examples:
 *   mvn test -Ddb.url=jdbc:mysql://host:3306/mydb -Ddb.user=myuser -Ddb.pass=secret
 *   mvn test -Ddb.url=jdbc:postgresql://host:5432/mydb -Ddb.user=postgres -Ddb.pass=secret
 */
public final class DbUtils {
    private DbUtils() {}

    // ------- Resolve connection details from sys props / config -------
    private static String cfg(String key, String def) {
        return ConfigReader.getOrDefault(key, def);
    }

    private static String dbUrl()  { return cfg("db.url",  ""); }
    private static String dbUser() { return cfg("db.user", ""); }
    private static String dbPass() { return cfg("db.pass", ""); }
    private static String dbDriver() { return cfg("db.driver", ""); } // optional: driver class name

    /**
     * Get a JDBC connection using the configured URL/user/pass.
     * Supports optional explicit driver class via -Ddb.driver
     */
    public static Connection getConnection() {
        String url = dbUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("❌ DB URL is missing. Provide -Ddb.url or config.properties (db.url)");
        }
        try {
            // Optional explicit driver load (helpful for some runtimes)
            String driver = dbDriver();
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
            // When user/pass are empty, try no-auth (e.g., Azure MSI, IAM auth, etc.)
            if ((dbUser() == null || dbUser().isBlank()) && (dbPass() == null || dbPass().isBlank())) {
                return DriverManager.getConnection(url);
            }
            return DriverManager.getConnection(url, dbUser(), dbPass());
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to obtain DB connection: " + e.getMessage(), e);
        }
    }

    // -------------------- Query helpers (SELECT) --------------------

    /**
     * Fetch multiple rows: SELECT ... (with optional params)
     * Returns each row as Map<columnName, value> preserving column order.
     */
    public static List<Map<String, Object>> fetchRows(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return toList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("❌ fetchRows failed: " + e.getMessage() + " | SQL=" + sql, e);
        }
    }

    /**
     * Fetch a single row (first row) or empty map if none.
     */
    public static Map<String, Object> fetchOne(String sql, Object... params) {
        List<Map<String, Object>> rows = fetchRows(sql, params);
        return rows.isEmpty() ? Collections.emptyMap() : rows.get(0);
    }

    /**
     * Fetch a single scalar value (first row, first column), or null if none.
     */
    public static Object fetchScalar(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("❌ fetchScalar failed: " + e.getMessage() + " | SQL=" + sql, e);
        }
    }

    // -------------------- Write helpers (INSERT/UPDATE/DELETE) --------------------

    /**
     * Execute DML (INSERT/UPDATE/DELETE). Returns rows affected.
     */
    public static int executeUpdate(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("❌ executeUpdate failed: " + e.getMessage() + " | SQL=" + sql, e);
        }
    }

    // -------------------- Internal utilities --------------------

    /** Bind PreparedStatement parameters safely (1-indexed). */
    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p == null) {
                ps.setObject(i + 1, null);
            } else if (p instanceof java.util.Date) {
                ps.setTimestamp(i + 1, new Timestamp(((java.util.Date) p).getTime()));
            } else if (p instanceof java.time.LocalDateTime) {
                ps.setTimestamp(i + 1, Timestamp.valueOf((java.time.LocalDateTime) p));
            } else if (p instanceof java.time.LocalDate) {
                ps.setDate(i + 1, Date.valueOf((java.time.LocalDate) p));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }

    /** Convert ResultSet to a List of Maps with column names. */
    private static List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int c = 1; c <= cols; c++) {
                String name = md.getColumnLabel(c);
                if (name == null || name.isBlank()) name = md.getColumnName(c);
                row.put(name, rs.getObject(c));
            }
            out.add(row);
        }
        return out;
    }
}
