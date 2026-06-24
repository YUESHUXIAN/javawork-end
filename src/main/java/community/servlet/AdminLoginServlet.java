package community.servlet;
import community.DAO.AdminDAO;
import community.entity.Admin;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private AdminDAO adminDao = new AdminDAO();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        // 获取前端参数：账号、密码、模拟U盾编码
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ukeyCode = request.getParameter("ukeyCode");

        Admin admin = adminDao.loginCheck(username, password, ukeyCode);
        if (admin != null) {
            // 登录成功，存入session
            HttpSession session = request.getSession();
            session.setAttribute("loginAdmin", admin);
            json.put("code", 200);
            json.put("msg", "登录成功");
        } else {
            json.put("code", 500);
            json.put("msg", "账号/密码/U盾编码错误，校验失败");
        }
        out.write(json.toString());
        out.flush();
        out.close();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}