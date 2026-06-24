package community.DAO;

import community.entity.OwnerApply;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OwnerApplyDAO {
    private Connection conn;
    private PreparedStatement pstmt;

    /**
     * 新增业主信息修改申请
     * @param ownerId 业主ID
     * @param newPhone 新手机号
     * @param newRoom 新门牌号
     * @param newPwd 新门禁密码
     * @return 受影响行数，>0代表插入成功
     */
    public int insertApply(Integer ownerId, String newPhone, String newRoom, String newPwd) {
        int rows = 0;
        // sql：申请时间自动取当前时间，状态默认0待审核
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
            // 关闭连接资源
            DBUtil.close(conn, pstmt);
        }
        return rows;
    }
}