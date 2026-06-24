package community.DAO;

import community.entity.Building;
import community.utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BuildingDAO {
    public int addBuilding(Building b) {
        String sql = "INSERT INTO building (build_name, build_no) VALUES (?, ?)";
        return DBUtil.update(sql, b.getBuildName(), b.getBuildNo());
    }

    public int updateBuilding(Integer id, String newName, String newNo) {
        String sql = "UPDATE building SET build_name=?, build_no=? WHERE id=?";
        return DBUtil.update(sql, newName, newNo, id);
    }

    public int deleteBuilding(Integer id) {
        String sql = "DELETE FROM building WHERE id=?";
        return DBUtil.update(sql, id);
    }

    public boolean isBuildNoExists(String no) {
        String sql = "SELECT COUNT(*) FROM building WHERE build_no=?";
        Object o = DBUtil.queryValue(sql, no);
        if (o == null) return false;
        return ((Number)o).longValue() > 0;
    }

    public boolean isBuildNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM building WHERE build_name=?";
        Object o = DBUtil.queryValue(sql, name);
        if (o == null) return false;
        return ((Number)o).longValue() > 0;
    }

    // 检查名称是否已存在（修改时需排除当前楼宇的ID）
    public boolean isBuildNameExists(String buildName, Integer excludeId) {
        String sql = "SELECT count(*) FROM building WHERE build_name=? AND id != ?";
        Connection conn = DBUtil.getConn();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, buildName);
            ps.setObject(2, excludeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }

    // 查询所有楼宇（供下拉框使用）
    public List<Building> getAllBuildings() {
        List<Building> list = new ArrayList<>();
        String sql = "SELECT id, build_name, build_no FROM building ORDER BY id";
        try (java.sql.Connection conn = DBUtil.getConn();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Building b = new Building();
                b.setId(rs.getInt("id"));
                b.setBuildName(rs.getString("build_name"));
                b.setBuildNo(rs.getString("build_no"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public void updateBuilding(Building b) {
        String sql = "UPDATE building SET build_name=?, build_no=? WHERE id=?";
        DBUtil.update(sql, b.getBuildName(), b.getBuildNo(), b.getId());
    }

    // 2. 检查编号是否已存在 (用于添加和修改时的唯一性校验)
// 修改时需排除当前楼宇的ID
    public boolean isBuildNoExists(String buildNo, Integer excludeId) {
        String sql = "SELECT count(*) FROM building WHERE build_no=? AND id != ?";
        Connection conn = DBUtil.getConn();
        PreparedStatement ps = null;
        ResultSet rs = null; // 1. 把 rs 提到外面声明
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, buildNo);
            ps.setObject(2, excludeId);
            rs = ps.executeQuery(); // 2. 赋值
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3. 正确关闭所有资源！注意看这里，ps和rs都传进去了
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }
}
