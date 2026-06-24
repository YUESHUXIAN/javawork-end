package community.servlet;

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

@WebServlet("/gate/check")
public class GateCheckServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        String name = request.getParameter("name");
        String pwd = request.getParameter("pwd");
        Owner owner = ownerDao.getOwnerByNamePwd(name,pwd);

        if(owner == null){
            json.put("code",501);
            json.put("msg","姓名或密码错误");
        }else if(owner.getIsConfirm() == 0){
            json.put("code",502);
            json.put("msg","您未完善业主信息，无法进门，请先提交信息修改申请");
        }else{
            json.put("code",200);
            json.put("msg","校验通过，门已打开");
        }
        out.write(json.toString());
    }
}