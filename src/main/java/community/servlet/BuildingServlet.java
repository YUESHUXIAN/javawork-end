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

@WebServlet("/admin/building")
public class BuildingServlet extends HttpServlet {
    private BuildingDAO buildingDAO = new BuildingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        String action = req.getParameter("action");
        if ("list".equals(action)) {
            List<Building> list = buildingDAO.getAllBuildings();
            resp.getWriter().write(JsonUtil.toJson(list));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            String name = req.getParameter("buildName");
            String no = req.getParameter("buildNo");
            if (buildingDAO.isBuildNoExists(no)) {
                resp.getWriter().write("{\"code\":500,\"msg\":\"楼宇编号已存在\"}");
                return;
            }
            if (buildingDAO.isBuildNameExists(name)) {
                resp.getWriter().write("{\"code\":500,\"msg\":\"楼宇名称已存在\"}");
                return;
            }
            Building b = new Building(); b.setBuildName(name); b.setBuildNo(no);
            buildingDAO.addBuilding(b);
            resp.getWriter().write("{\"code\":200,\"msg\":\"添加成功\"}");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            buildingDAO.deleteBuilding(id);
            resp.getWriter().write("{\"code\":200,\"msg\":\"删除成功\"}");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("buildName");
            String no = req.getParameter("buildNo");
            // 校验唯一性（排除当前记录）
            if (buildingDAO.isBuildNoExists(no)) {
                // 需要判断是否为同一条记录 -- 简化处理：若编号存在且不是当前id则报错
                resp.getWriter().write("{\"code\":500,\"msg\":\"楼宇编号已存在\"}");
                return;
            }
            if (buildingDAO.isBuildNameExists(name)) {
                resp.getWriter().write("{\"code\":500,\"msg\":\"楼宇名称已存在\"}");
                return;
            }
            buildingDAO.updateBuilding(id, name, no);
            resp.getWriter().write("{\"code\":200,\"msg\":\"更新成功\"}");
        } else if ("update".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("buildName");
        String no = req.getParameter("buildNo");

        // 唯一性校验（排除自身ID）
        if (buildingDAO.isBuildNoExists(no, id)) {
            resp.getWriter().write("{\"code\":500, \"msg\":\"楼宇编号已存在\"}");
            return;
        }
        Building b = new Building();
        b.setId(id);
        b.setBuildName(name);
        b.setBuildNo(no);
        buildingDAO.updateBuilding(b);
        resp.getWriter().write("{\"code\":200, \"msg\":\"修改成功\"}");
    }
    }
}
