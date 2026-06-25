package community.servlet;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.PrintWriter;

@WebServlet("/admin/checkLogin")
public class CheckLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        HttpSession session = req.getSession();
        if (session.getAttribute("loginAdmin") != null) {
            json.put("code", 200);
            json.put("msg", "已登录");
        } else {
            json.put("code", 401);
            json.put("msg", "未登录");
        }
        try {
            PrintWriter out = resp.getWriter();
            out.write(json.toJSONString());
            out.flush();
        } catch (Exception ignored) {}
    }
}
