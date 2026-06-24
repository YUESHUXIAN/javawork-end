package community.servlet;

import community.DAO.AccessRecordDAO;
import community.entity.AccessRecord;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/queryRecord")
public class RecordQueryServlet extends HttpServlet {
    private AccessRecordDAO recordDao = new AccessRecordDAO();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        // 登录拦截
        HttpSession session = request.getSession();
        if(session.getAttribute("loginAdmin") == null){
            json.put("code",401);
            json.put("msg","请先登录管理员");
            out.write(json.toString());
            return;
        }
        String name = request.getParameter("name");
        String start = request.getParameter("startTime");
        String end = request.getParameter("endTime");
        List<AccessRecord> list = recordDao.query(name,start,end);
        json.put("code",200);
        json.put("data", JSONArray.parseArray(JSONObject.toJSONString(list)));
        out.write(json.toString());
    }
}