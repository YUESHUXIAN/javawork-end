package community.servlet;

import community.DAO.OwnerApplyDAO;
import community.DAO.OwnerDAO;
import community.entity.Owner;
import community.entity.OwnerApply;
import community.utils.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/owner")
public class OwnerServlet extends HttpServlet {
    private OwnerDAO ownerDAO = new OwnerDAO();
    private OwnerApplyDAO applyDAO = new OwnerApplyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");

        // 登录拦截
        HttpSession session = req.getSession();
        if (session.getAttribute("loginAdmin") == null) {
            resp.getWriter().write("{\"code\":401,\"msg\":\"请先登录管理员\"}");
            return;
        }

        String action = req.getParameter("action");
        if ("list".equals(action)) {
            List<Owner> owners = ownerDAO.getAllOwners();
            resp.getWriter().write(JsonUtil.toJson(owners));
        } else if ("applyList".equals(action)) {
            List<OwnerApply> applies = applyDAO.getAllApplies(); // 需在ApplyDAO中补充此方法
            resp.getWriter().write(JsonUtil.toJson(applies));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        // 登录拦截
        HttpSession session = req.getSession();
        if (session.getAttribute("loginAdmin") == null) {
            resp.getWriter().write("{\"code\":401,\"msg\":\"请先登录管理员\"}");
            return;
        }

        String action = req.getParameter("action");

        if ("addInit".equals(action)) {
            // 管理员初始化新增
            String name = req.getParameter("name");
            String idCard = req.getParameter("idCard");
            int buildId = Integer.parseInt(req.getParameter("buildId"));
            ownerDAO.addInitOwner(name, idCard, buildId);
            resp.getWriter().write("{\"code\":200,\"msg\":\"初始化成功\"}");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            ownerDAO.deleteOwner(id);
            resp.getWriter().write("{\"code\":200,\"msg\":\"删除成功\"}");
        } else if ("approveApply".equals(action)) {
            // 管理员审批修改申请
            int applyId = Integer.parseInt(req.getParameter("applyId"));
            // 先查申请详情
            OwnerApply apply = applyDAO.getApplyById(applyId);
            if(apply != null){
                ownerDAO.updateOwnerInfo(apply.getOwnerId(), apply.getNewPhone(), apply.getNewRoom(), apply.getNewPwd());
                applyDAO.updateStatus(applyId, 1); // 标记为已审批
                resp.getWriter().write("{\"code\":200,\"msg\":\"审核通过，业主信息已更新\"}");
            } else {
                resp.getWriter().write("{\"code\":500,\"msg\":\"申请不存在\"}");
            }
        }
    }
}