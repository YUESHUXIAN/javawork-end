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
     * @param name 模糊姓名
     * @param start 进入起始时间
     * @param end 进入结束时间
     * @return 记录集合
     */
    public List<AccessRecord> query(String name, String start, String end) {
        List<AccessRecord> recordList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM access_record WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        // 拼接动态查询条件
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
            // 填充占位符
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();
            // 封装实体
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
}