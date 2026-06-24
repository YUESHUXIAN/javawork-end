package community.DAO;

import community.entity.Admin;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    /**
     * 登录三重校验：账号、密码、模拟U盾编码
     */
    public Admin loginCheck(String username, String password, String ukeyCode) {
        Connection conn = DBUtil.getConn();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Admin admin = null;
        String sql = "SELECT id,username FROM admin WHERE username=? AND password=? AND ukey_code=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, ukeyCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return admin;
    }
}