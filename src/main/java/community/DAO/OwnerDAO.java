package community.DAO;

import community.entity.Owner;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OwnerDAO {
    /**
     * 业主提交申请时身份校验：姓名+身份证+楼栋ID匹配原始数据
     */
    public Owner findOwnerByInfo(String name, String idCard, Integer buildId) {
        Connection conn = DBUtil.getConn();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Owner owner = null;
        String sql = "SELECT id,is_confirm FROM owner WHERE name=? AND id_card=? AND build_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, idCard);
            pstmt.setInt(3, buildId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                owner = new Owner();
                owner.setId(rs.getInt("id"));
                owner.setIsConfirm(rs.getInt("is_confirm"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return owner;
    }

    /**
     * 门禁开门校验：姓名+密码，返回确认状态is_confirm
     */
    public Owner getOwnerByNamePwd(String name, String pwd) {
        Connection conn = DBUtil.getConn();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Owner owner = null;
        String sql = "SELECT is_confirm FROM owner WHERE name=? AND entry_pwd=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, pwd);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                owner = new Owner();
                owner.setIsConfirm(rs.getInt("is_confirm"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return owner;
    }
}