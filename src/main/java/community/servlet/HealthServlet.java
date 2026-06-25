package community.servlet;

import com.alibaba.fastjson.JSONObject;
import community.utils.DBUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        Connection conn = null;
        try {
            conn = DBUtil.getConn();
            if (conn == null) {
                json.put("db", "连接失败: getConn()返回null");
            } else {
                json.put("db", "连接成功");
                // 检查表
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SHOW TABLES");
                StringBuilder tables = new StringBuilder();
                while (rs.next()) {
                    if (tables.length() > 0) tables.append(", ");
                    tables.append(rs.getString(1));
                }
                json.put("tables", tables.length() > 0 ? tables.toString() : "无表");
                rs.close();

                // 检查admin表数据
                try {
                    rs = st.executeQuery("SELECT COUNT(*) FROM admin");
                    if (rs.next()) json.put("admin_count", rs.getInt(1));
                    rs.close();
                } catch (Exception e) {
                    json.put("admin_error", e.getMessage());
                }

                // 检查building表数据
                try {
                    rs = st.executeQuery("SELECT COUNT(*) FROM building");
                    if (rs.next()) json.put("building_count", rs.getInt(1));
                    rs.close();
                } catch (Exception e) {
                    json.put("building_error", e.getMessage());
                }

                st.close();
            }
        } catch (Exception e) {
            json.put("error", e.getMessage());
        } finally {
            DBUtil.close(conn, null);
        }
        try {
            PrintWriter out = resp.getWriter();
            out.write(json.toJSONString());
            out.flush();
        } catch (Exception ignored) {}
    }
}
