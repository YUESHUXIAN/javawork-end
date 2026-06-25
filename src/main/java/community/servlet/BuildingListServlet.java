package community.servlet;

import community.DAO.BuildingDAO;
import community.entity.Building;
import community.utils.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 公共楼宇列表接口（无需登录）
 * 供业主端、门禁端等非管理员页面使用
 */
@WebServlet("/api/buildings")
public class BuildingListServlet extends HttpServlet {
    private BuildingDAO buildingDAO = new BuildingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            List<Building> list = buildingDAO.getAllBuildings();
            resp.getWriter().write(JsonUtil.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"code\":500,\"msg\":\"获取楼宇列表失败\"}");
        }
    }
}
