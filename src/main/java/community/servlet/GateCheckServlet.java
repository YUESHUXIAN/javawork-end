package community.servlet;

import community.DAO.AccessRecordDAO;
import community.DAO.OwnerDAO;
import community.entity.AccessRecord;
import community.entity.Owner;
import com.alibaba.fastjson.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;

@WebServlet("/gate/check")
public class GateCheckServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();
    private AccessRecordDAO recordDao = new AccessRecordDAO();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RAND = new SecureRandom();

    private String genTempPwd(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(CHARS.charAt(RAND.nextInt(CHARS.length())));
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        String action = request.getParameter("action"); // "enter" or "leave" (default enter)
        if (action == null) action = "enter";
        String isOwner = request.getParameter("isOwner"); // "1" 或 "0"

        if ("enter".equals(action)) {
            if ("1".equals(isOwner)) {
                String name = request.getParameter("name");
                String pwd = request.getParameter("pwd");
                Owner owner = ownerDao.getOwnerByNameAndPwd(name, pwd);
                if (owner == null) {
                    json.put("code",501); json.put("msg","姓名或密码错误");
                } else if (owner.getIsConfirm() == null || owner.getIsConfirm() == 0) {
                    json.put("code",502); json.put("msg","您未完善业主信息，无法进门，请先提交信息修改申请");
                } else {
                    // 记录进门
                    AccessRecord r = new AccessRecord();
                    r.setName(owner.getName()); r.setIsOwner(1);
                    r.setIdCard(owner.getIdCard()); r.setPhone(owner.getPhone()); r.setOwnerId(owner.getId());
                    recordDao.addRecord(r);
                    json.put("code",200); json.put("msg","校验通过，门已打开");
                }
            } else {
                // 访客流程：输入姓名、身份证号、手机号、来访事由。生成临时密码并返回。
                String name = request.getParameter("name");
                String idCard = request.getParameter("idCard");
                String phone = request.getParameter("phone");
                // 生成 8 位临时密码
                String tempPwd = genTempPwd(8);
                AccessRecord r = new AccessRecord();
                r.setName(name); r.setIsOwner(0); r.setIdCard(idCard); r.setPhone(phone); r.setTempPwd(tempPwd);
                recordDao.addRecord(r);
                json.put("code",200); json.put("msg","访客临时密码已生成"); json.put("tempPwd", tempPwd);
            }
        } else if ("leave".equals(action)) {
            if ("1".equals(isOwner)) {
                String name = request.getParameter("name");
                String pwd = request.getParameter("pwd");
                Owner owner = ownerDao.getOwnerByNameAndPwd(name, pwd);
                if (owner == null) {
                    json.put("code",501); json.put("msg","姓名或密码错误");
                } else {
                    recordDao.updateLeaveTimeByName(name);
                    json.put("code",200); json.put("msg","离开记录已保存");
                }
            } else {
                String name = request.getParameter("name");
                String tempPwd = request.getParameter("tempPwd");
                if (recordDao.checkVisitorTempPwd(name, tempPwd)) {
                    recordDao.updateLeaveTimeByName(name);
                    json.put("code",200); json.put("msg","访客离开记录已保存");
                } else {
                    json.put("code",501); json.put("msg","临时密码错误或记录不存在");
                }
            }
        }

        out.write(json.toString());
        out.flush();
        out.close();
    }
}
