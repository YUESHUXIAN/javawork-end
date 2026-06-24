package community.servlet;

import community.DAO.OwnerApplyDAO;
import community.DAO.OwnerDAO;
import community.entity.Owner;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/owner/submitApply")
public class OwnerApplyServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO ();
    private OwnerApplyDAO  applyDao = new OwnerApplyDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        // 前端提交校验身份参数
        String name = request.getParameter("name");
        String idCard = request.getParameter("idCard");
        Integer buildId = Integer.parseInt(request.getParameter("buildId"));
        String newPhone = request.getParameter("newPhone");
        String newRoom = request.getParameter("newRoom");
        String newPwd = request.getParameter("newPwd");

        // 1. 校验原始业主信息是否匹配
        Owner owner = ownerDao.findOwnerByInfo(name, idCard, buildId);
        if (owner == null) {
            json.put("code", 500);
            json.put("msg", "姓名、身份证、楼栋信息不匹配，无法提交申请");
            out.write(json.toString());
            return;
        }
        // 2. 插入修改申请
        int res = applyDao.insertApply(owner.getId(), newPhone, newRoom, newPwd);
        if (res > 0) {
            json.put("code", 200);
            json.put("msg", "申请提交成功，等待管理员审核，审核通过后可正常进门");
        } else {
            json.put("code", 500);
            json.put("msg", "申请提交失败");
        }
        out.write(json.toString());
    }
}