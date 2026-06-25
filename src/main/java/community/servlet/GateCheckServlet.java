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

        String action = request.getParameter("action"); // "enter", "leave", "visitorRegister"
        if (action == null) action = "enter";
        String isOwner = request.getParameter("isOwner"); // "1" 或 "0"

        try {
            if ("enter".equals(action)) {
                if ("1".equals(isOwner)) {
                    // 业主进门：姓名+密码验证
                    String name = request.getParameter("name");
                    String pwd = request.getParameter("pwd");
                    Owner owner = ownerDao.getOwnerByNameAndPwd(name, pwd);
                    if (owner == null) {
                        json.put("code", 501);
                        json.put("msg", "姓名或密码错误");
                    } else if (owner.getIsConfirm() == null || owner.getIsConfirm() == 0) {
                        json.put("code", 502);
                        json.put("msg", "您未完善业主信息，无法进门，请先提交信息修改申请");
                    } else {
                        // 记录进门
                        AccessRecord r = new AccessRecord();
                        r.setName(owner.getName());
                        r.setIsOwner(1);
                        r.setIdCard(owner.getIdCard());
                        r.setPhone(owner.getPhone());
                        r.setOwnerId(owner.getId());
                        recordDao.addRecord(r);
                        json.put("code", 200);
                        json.put("msg", "校验通过，门已打开，欢迎回家！");
                    }
                } else {
                    // 访客用临时密码进门（已注册，验证密码）
                    String name = request.getParameter("name");
                    String tempPwd = request.getParameter("tempPwd");
                    if (name == null || name.trim().isEmpty() || tempPwd == null || tempPwd.trim().isEmpty()) {
                        json.put("code", 500);
                        json.put("msg", "请输入姓名和临时密码");
                    } else if (recordDao.checkVisitorTempPwd(name.trim(), tempPwd.trim())) {
                        json.put("code", 200);
                        json.put("msg", "验证通过，门已打开，欢迎来访！");
                    } else {
                        json.put("code", 501);
                        json.put("msg", "姓名或临时密码错误，或记录不存在");
                    }
                }
            } else if ("visitorRegister".equals(action)) {
                // 访客登记：输入姓名、身份证、手机号，生成临时密码并记录进入
                String name = request.getParameter("name");
                String idCard = request.getParameter("idCard");
                String phone = request.getParameter("phone");
                if (name == null || name.trim().isEmpty()) {
                    json.put("code", 500);
                    json.put("msg", "请输入访客姓名");
                } else {
                    String tempPwd = genTempPwd(8);
                    AccessRecord r = new AccessRecord();
                    r.setName(name.trim());
                    r.setIsOwner(0);
                    r.setIdCard(idCard != null ? idCard.trim() : "");
                    r.setPhone(phone != null ? phone.trim() : "");
                    r.setTempPwd(tempPwd);
                    recordDao.addRecord(r);
                    json.put("code", 200);
                    json.put("msg", "访客登记成功，请记住临时密码");
                    json.put("tempPwd", tempPwd);
                }
            } else if ("leave".equals(action)) {
                if ("1".equals(isOwner)) {
                    // 业主离开：姓名+密码
                    String name = request.getParameter("name");
                    String pwd = request.getParameter("pwd");
                    Owner owner = ownerDao.getOwnerByNameAndPwd(name, pwd);
                    if (owner == null) {
                        json.put("code", 501);
                        json.put("msg", "姓名或密码错误");
                    } else {
                        recordDao.updateLeaveTimeByName(name);
                        json.put("code", 200);
                        json.put("msg", "离开记录已保存，再见！");
                    }
                } else {
                    // 访客离开：只需临时密码
                    String tempPwd = request.getParameter("tempPwd");
                    if (tempPwd == null || tempPwd.trim().isEmpty()) {
                        json.put("code", 500);
                        json.put("msg", "请输入临时密码");
                    } else {
                        // 查找使用该临时密码且未离开的记录
                        String name = recordDao.findVisitorNameByTempPwd(tempPwd.trim());
                        if (name != null) {
                            recordDao.updateLeaveTimeByName(name);
                            json.put("code", 200);
                            json.put("msg", "访客离开记录已保存，再见！");
                        } else {
                            json.put("code", 501);
                            json.put("msg", "临时密码错误或记录不存在");
                        }
                    }
                }
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
