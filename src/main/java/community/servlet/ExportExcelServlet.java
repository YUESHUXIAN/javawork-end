package community.servlet;

import community.DAO.AccessRecordDAO;
import community.entity.AccessRecord;
import community.utils.ExcelUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/admin/export")
public class ExportExcelServlet extends HttpServlet {
    private AccessRecordDAO dao = new AccessRecordDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session.getAttribute("loginAdmin") == null){
            response.getWriter().print("未登录禁止导出");
            return;
        }
        String name = request.getParameter("name");
        String start = request.getParameter("startTime");
        String end = request.getParameter("endTime");
        List<AccessRecord> list = dao.query(name,start,end);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment;filename=进出记录.xlsx");
        OutputStream os = response.getOutputStream();
        try {
            ExcelUtil.export(list,os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        os.flush();
        os.close();
    }
}