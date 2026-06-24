package community.DAO;

import community.entity.AccessRecord;
import community.utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessRecordDAO {
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    /**
     * 多条件查询进出记录：姓名模糊、开始时间、结束时间
     */
    public List<AccessRecord> query(String name, String start, String end) {
        List<AccessRecord> recordList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM access_record WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND name LIKE ? ");
            params.add("%" + name.trim() + "%");
        }
        if (start != null && !start.trim().isEmpty()) {
            sql.append(" AND enter_time >= ? ");
            params.add(start);
        }
        if (end != null && !end.trim().isEmpty()) {
            sql.append(" AND enter_time <= ? ");
            params.add(end);
        }
        sql.append(" ORDER BY enter_time DESC");

        conn = DBUtil.getConn();
        try {
            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AccessRecord record = new AccessRecord();
                record.setId(rs.getInt("id"));
                record.setName(rs.getString("name"));
                record.setIsOwner(rs.getInt("is_owner"));
                record.setIdCard(rs.getString("id_card"));
                record.setPhone(rs.getString("phone"));
                record.setEnterTime(rs.getTimestamp("enter_time"));
                record.setLeaveTime(rs.getTimestamp("leave_time"));
                record.setTempPwd(rs.getString("temp_pwd"));
                record.setOwnerId(rs.getInt("owner_id"));
                recordList.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return recordList;
    }

    public void addRecord(AccessRecord record) {
        String sql = "INSERT INTO access_record (name, is_owner, id_card, phone, enter_time, temp_pwd, owner_id) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        DBUtil.update(sql, record.getName(), record.getIsOwner(), record.getIdCard(), record.getPhone(), record.getTempPwd(), record.getOwnerId());
    }

    // 2. 离开时更新离开时间（业主/访客通用）
    public void updateLeaveTimeByName(String name) {
        String sql = "UPDATE access_record SET leave_time = NOW() WHERE name = ? AND leave_time IS NULL ORDER BY enter_time DESC LIMIT 1";
        DBUtil.update(sql, name);
    }

    // 3. 访客离开时校验临时密码
    public boolean checkVisitorTempPwd(String name, String tempPwd) {
        String sql = "SELECT COUNT(*) FROM access_record WHERE name=? AND temp_pwd=? AND leave_time IS NULL";
        Object val = DBUtil.queryValue(sql, name, tempPwd);
        if (val == null) return false;
        return ((Number)val).longValue() > 0;
    }
}
