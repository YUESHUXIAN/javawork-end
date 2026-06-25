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

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String ukeyCode = request.getParameter("ukeyCode");

            System.out.println("[AdminLogin] 收到登录请求: username=" + username + ", password=" + password + ", ukeyCode=" + ukeyCode);

            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                ukeyCode == null || ukeyCode.trim().isEmpty()) {
                json.put("code", 500);
                json.put("msg", "请填写完整的登录信息");
                out.write(json.toString());
                out.flush();
                return;
            }

            Admin admin = adminDao.loginCheck(username.trim(), password.trim(), ukeyCode.trim());
            if (admin != null) {
                HttpSession session = request.getSession();
                session.setAttribute("loginAdmin", admin);
                json.put("code", 200);
                json.put("msg", "登录成功");
                System.out.println("[AdminLogin] 登录成功: " + admin.getUsername());
            } else {
                json.put("code", 500);
                json.put("msg", "账号/密码/U盾编码错误");
                System.out.println("[AdminLogin] 登录失败: 账号密码不匹配");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", 500);
            json.put("msg", "系统错误: " + e.getMessage());
            System.out.println("[AdminLogin] 异常: " + e.getMessage());
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
