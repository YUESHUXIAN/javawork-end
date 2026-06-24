package community.servlet;

import community.DAO.OwnerApplyDAO;
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

@WebServlet("/owner/submitApply")
public class OwnerApplyServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();
    private OwnerApplyDAO applyDao = new OwnerApplyDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        String newPhone = request.getParameter("newPhone");
        String newRoom = request.getParameter("newRoom");
        String newPwd = request.getParameter("newPwd");

        if (newPhone == null || newPhone.trim().isEmpty() ||
            newRoom == null || newRoom.trim().isEmpty() ||
            newPwd == null || newPwd.trim().isEmpty()) {
            json.put("code", 500);
            json.put("msg", "请填写完整的修改信息");
            out.write(json.toString());
            return;
        }

        Integer ownerId = null;

        // 方式一：从 Session 获取已核验的业主 ID（verify.html 使用）
        HttpSession session = request.getSession();
        Integer verifiedOwnerId = (Integer) session.getAttribute("verifiedOwnerId");

        if (verifiedOwnerId != null) {
            ownerId = verifiedOwnerId;
        } else {
            // 方式二：前端传了身份信息则自行验证（ownerApply.html 使用）
            String name = request.getParameter("name");
            String idCard = request.getParameter("idCard");
            String buildIdStr = request.getParameter("buildId");

            if (name != null && !name.trim().isEmpty() &&
                idCard != null && !idCard.trim().isEmpty() &&
                buildIdStr != null && !buildIdStr.trim().isEmpty()) {
                try {
                    Integer buildId = Integer.parseInt(buildIdStr);
                    Owner owner = ownerDao.findOwnerByInfo(name.trim(), idCard.trim(), buildId);
                    if (owner != null) {
                        ownerId = owner.getId();
                    } else {
                        json.put("code", 500);
                        json.put("msg", "姓名、身份证、楼栋信息不匹配，无法提交申请");
                        out.write(json.toString());
                        return;
                    }
                } catch (NumberFormatException e) {
                    json.put("code", 500);
                    json.put("msg", "楼栋ID格式错误");
                    out.write(json.toString());
                    return;
                }
            }
        }

        if (ownerId == null) {
            json.put("code", 401);
            json.put("msg", "请先进行身份核验");
            out.write(json.toString());
            return;
        }

        // 插入修改申请
        try {
            int res = applyDao.insertApply(ownerId, newPhone.trim(), newRoom.trim(), newPwd.trim());
            if (res > 0) {
                json.put("code", 200);
                json.put("msg", "申请提交成功，等待管理员审核，审核通过后可正常进门");
            } else {
                json.put("code", 500);
                json.put("msg", "申请提交失败");
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