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
        String sql = "SELECT id,is_confirm FROM owner WHERE name=? AND id_card=? AND build_id=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, idCard);
            pstmt.setInt(3, buildId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Owner owner = new Owner();
                    owner.setId(rs.getInt("id"));
                    owner.setIsConfirm(rs.getInt("is_confirm"));
                    return owner;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 门禁开门校验：姓名+密码，返回确认状态is_confirm
     */
    public Owner getOwnerByNamePwd(String name, String pwd) {
        String sql = "SELECT is_confirm FROM owner WHERE name=? AND entry_pwd=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, pwd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Owner owner = new Owner();
                    owner.setIsConfirm(rs.getInt("is_confirm"));
                    return owner;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    // 管理员审批时只更新非空字段
    public void updateOwnerInfoSelective(Integer ownerId, String newPhone, String newRoom, String newPwd) {
        StringBuilder sql = new StringBuilder("UPDATE owner SET is_confirm=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (newPhone != null) { sql.append(", phone=?"); params.add(newPhone); }
        if (newRoom != null) { sql.append(", room_no=?"); params.add(newRoom); }
        if (newPwd != null) { sql.append(", entry_pwd=?"); params.add(newPwd); }
        sql.append(" WHERE id=?");
        params.add(ownerId);
        DBUtil.update(sql.toString(), params.toArray());
    }

    // 管理员修改业主基本信息（姓名、身份证、楼宇）
    public void updateOwner(Integer id, String name, String idCard, Integer buildId) {
        String sql = "UPDATE owner SET name=?, id_card=?, build_id=? WHERE id=?";
        DBUtil.update(sql, name, idCard, buildId, id);
    }

    // 更新业主确认状态
    public void updateOwnerConfirm(Integer ownerId, int confirm) {
        String sql = "UPDATE owner SET is_confirm=? WHERE id=?";
        DBUtil.update(sql, confirm, ownerId);
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
