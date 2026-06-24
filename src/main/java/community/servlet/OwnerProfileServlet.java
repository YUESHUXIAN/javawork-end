package community.servlet;

import community.DAO.OwnerDAO;
import community.entity.Owner;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/owner/profile")
public class OwnerProfileServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        // 从Session获取已核验的业主ID
        HttpSession session = request.getSession();
        Integer ownerId = (Integer) session.getAttribute("verifiedOwnerId");

        if (ownerId == null) {
            json.put("code", 401);
            json.put("msg", "请先进行身份核验");
            out.write(json.toString());
            return;
        }

        try {
            // 根据ID查询业主完整信息
            Owner owner = ownerDao.getOwnerById(ownerId);

            if (owner != null) {
                json.put("code", 200);
                json.put("name", owner.getName());
                json.put("idCard", owner.getIdCard());
                json.put("phone", owner.getPhone() != null ? owner.getPhone() : "");
                json.put("buildId", owner.getBuildId());
                json.put("roomNo", owner.getRoomNo() != null ? owner.getRoomNo() : "");
                json.put("entryPwd", owner.getEntryPwd() != null ? owner.getEntryPwd() : "");
                json.put("isConfirm", owner.getIsConfirm());
            } else {
                json.put("code", 500);
                json.put("msg", "未找到业主信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", 500);
            json.put("msg", "系统错误，请稍后重试");
        }

        out.write(json.toString());
        out.flush();
        out.close();
    }
}
