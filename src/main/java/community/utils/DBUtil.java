package community.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static volatile HikariDataSource dataSource;

    static {
        initDataSource();
    }

    private static void initDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://localhost:3306/community_access?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8");
            config.setUsername("root");
            config.setPassword("123456");

            config.setMinimumIdle(5);
            config.setMaximumPoolSize(20);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            // 预热：执行一条简单查询确保连接真正可用
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT 1")) {
                ps.executeQuery();
                System.out.println("[DBUtil] 数据库连接池初始化成功");
            }
        } catch (Exception e) {
            System.err.println("[DBUtil] 数据库连接池初始化失败: " + e.getMessage());
            dataSource = null;
        }
    }

    /**
     * 获取数据库连接，如果连接池未初始化则尝试重新初始化
     */
    public static Connection getConn() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (DBUtil.class) {
                if (dataSource == null || dataSource.isClosed()) {
                    System.out.println("[DBUtil] 连接池不可用，尝试重新初始化...");
                    initDataSource();
                }
            }
        }
        if (dataSource == null) {
            System.err.println("[DBUtil] 连接池仍不可用");
            return null;
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }

    public static void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

    public static int update(String sql, Object... params) {
        try (Connection conn = getConn()) {
            if (conn == null) return 0;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
                }
                return pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Object queryValue(String sql, Object... params) {
        try (Connection conn = getConn()) {
            if (conn == null) return null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
