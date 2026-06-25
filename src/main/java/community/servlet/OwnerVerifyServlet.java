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

@WebServlet("/owner/verify")
public class OwnerVerifyServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        try {
            String name = request.getParameter("name");
            String idCard = request.getParameter("idCard");
            String buildIdStr = request.getParameter("buildId");

            if (name == null || name.trim().isEmpty() ||
                idCard == null || idCard.trim().isEmpty() ||
                buildIdStr == null || buildIdStr.trim().isEmpty()) {
                json.put("code", 500);
                json.put("msg", "请填写完整的核验信息");
                out.write(json.toString());
                return;
            }

            Integer buildId = Integer.parseInt(buildIdStr.trim());
            Owner owner = ownerDao.findOwnerByInfo(name.trim(), idCard.trim(), buildId);

            if (owner != null) {
                HttpSession session = request.getSession();
                session.setAttribute("verifiedOwnerId", owner.getId());
                session.setAttribute("verifiedOwnerName", name.trim());
                json.put("code", 200);
                json.put("msg", "身份核验成功");
                json.put("ownerId", owner.getId());
            } else {
                json.put("code", 500);
                json.put("msg", "姓名、身份证号或楼栋信息不匹配");
            }
        } catch (NumberFormatException e) {
            json.put("code", 500);
            json.put("msg", "楼栋ID格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", 500);
            json.put("msg", "系统错误: " + e.getMessage());
        }

        out.write(json.toString());
        out.flush();
        out.close();
    }
}
