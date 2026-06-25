package community.DAO;

import community.entity.Owner;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

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
    public List<Owner> getAllOwners() {
        List<Owner> list = new ArrayList<>();
        String sql = "SELECT * FROM owner ORDER BY id";
        try (Connection conn = DBUtil.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToOwner(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. 管理员新增/初始化业主 (根据截图需求，只填姓名、身份证、楼宇)
    public void addInitOwner(String name, String idCard, Integer buildId) {
        String sql = "INSERT INTO owner (name, id_card, build_id, entry_pwd, is_confirm) VALUES (?, ?, ?, '123456', 0)";
        DBUtil.update(sql, name, idCard, buildId);
    }

    // 3. 管理员更新业主信息（审核通过后调用）
    public void updateOwnerInfo(Integer ownerId, String newPhone, String newRoom, String newPwd) {
        String sql = "UPDATE owner SET phone=?, room_no=?, entry_pwd=?, is_confirm=1 WHERE id=?";
        DBUtil.update(sql, newPhone, newRoom, newPwd, ownerId);
    }

    // 管理员修改业主基本信息（姓名、身份证、楼宇）
    public void updateOwner(Integer id, String name, String idCard, Integer buildId) {
        String sql = "UPDATE owner SET name=?, id_card=?, build_id=? WHERE id=?";
        DBUtil.update(sql, name, idCard, buildId, id);
    }

    // 4. 管理员删除业主
    public void deleteOwner(Integer id) {
        String sql = "DELETE FROM owner WHERE id = ?";
        DBUtil.update(sql, id);
    }

    // 根据ID查询业主信息
    public Owner getOwnerById(Integer id) {
        String sql = "SELECT id, name, id_card, phone, build_id, room_no, entry_pwd, is_confirm FROM owner WHERE id=?";
        try (Connection conn = DBUtil.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToOwner(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 5. 门禁离开时：根据姓名和密码查询（业主/访客）
    public Owner getOwnerByNameAndPwd(String name, String pwd) {
        String sql = "SELECT id, is_confirm, name, id_card, phone, build_id, room_no, entry_pwd FROM owner WHERE name=? AND entry_pwd=?";
        try (Connection conn = DBUtil.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name); pstmt.setString(2, pwd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToOwner(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private Owner mapRowToOwner(ResultSet rs) throws SQLException {
        Owner o = new Owner();
        try {
            o.setId(rs.getInt("id"));
        } catch (Exception ignored) {}
        o.setName(rs.getString("name"));
        o.setIdCard(rs.getString("id_card"));
        o.setPhone(rs.getString("phone"));
        try { o.setBuildId(rs.getInt("build_id")); } catch (Exception ignored) {}
        o.setRoomNo(rs.getString("room_no"));
        o.setEntryPwd(rs.getString("entry_pwd"));
        try { o.setIsConfirm(rs.getInt("is_confirm")); } catch (Exception ignored) {}
        return o;
    }
}
