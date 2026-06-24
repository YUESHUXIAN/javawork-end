package community.DAO;

import community.entity.OwnerApply;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OwnerApplyDAO {
    private Connection conn;
    private PreparedStatement pstmt;

    /**
     * 新增业主信息修改申请
     */
    public int insertApply(Integer ownerId, String newPhone, String newRoom, String newPwd) {
        int rows = 0;
        String sql = "INSERT INTO owner_apply(owner_id, new_phone, new_room, new_pwd, apply_time, status) VALUES (?,?,?,?,NOW(),0)";
        conn = DBUtil.getConn();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ownerId);
            pstmt.setString(2, newPhone);
            pstmt.setString(3, newRoom);
            pstmt.setString(4, newPwd);
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return rows;
    }

    public java.util.List<OwnerApply> getAllApplies() {
        java.util.List<OwnerApply> list = new ArrayList<>();
        String sql = "SELECT id, owner_id, new_phone, new_room, new_pwd, apply_time, status FROM owner_apply ORDER BY apply_time DESC";
        try (Connection conn = DBUtil.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                OwnerApply a = new OwnerApply();
                a.setId(rs.getInt("id"));
                a.setOwnerId(rs.getInt("owner_id"));
                a.setNewPhone(rs.getString("new_phone"));
                a.setNewRoom(rs.getString("new_room"));
                a.setNewPwd(rs.getString("new_pwd"));
                a.setApplyTime(rs.getTimestamp("apply_time"));
                a.setStatus(rs.getInt("status"));
                list.add(a);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public OwnerApply getApplyById(int id) {
        String sql = "SELECT id, owner_id, new_phone, new_room, new_pwd, apply_time, status FROM owner_apply WHERE id=?";
        try (Connection conn = DBUtil.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OwnerApply a = new OwnerApply();
                    a.setId(rs.getInt("id"));
                    a.setOwnerId(rs.getInt("owner_id"));
                    a.setNewPhone(rs.getString("new_phone"));
                    a.setNewRoom(rs.getString("new_room"));
                    a.setNewPwd(rs.getString("new_pwd"));
                    a.setApplyTime(rs.getTimestamp("apply_time"));
                    a.setStatus(rs.getInt("status"));
                    return a;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public int updateStatus(int id, int status) {
        String sql = "UPDATE owner_apply SET status=? WHERE id=?";
        return DBUtil.update(sql, status, id);
    }
}
