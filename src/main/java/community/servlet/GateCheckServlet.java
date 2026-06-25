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
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/gate/check")
public class GateCheckServlet extends HttpServlet {
    private OwnerDAO ownerDao = new OwnerDAO();
    private AccessRecordDAO recordDao = new AccessRecordDAO();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RAND = new SecureRandom();

    // 存储访客临时密码和访客信息的映射（key: 临时密码, value: 访客信息）
    private static final ConcurrentHashMap<String, VisitorInfo> pendingVisitors = new ConcurrentHashMap<>();

    // 访客信息内部类
    private static class VisitorInfo {
        String name;
        String idCard;
        String phone;

        VisitorInfo(String name, String idCard, String phone) {
            this.name = name;
            this.idCard = idCard;
            this.phone = phone;
        }
    }

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
                    } else if (recordDao.hasUnfinishedRecord(owner.getName())) {
                        // 检查是否已有未离开的记录
                        json.put("code", 503);
                        json.put("msg", "您已在小区内，请勿重复进入");
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
                    // 访客用临时密码进门
                    String name = request.getParameter("name");
                    String tempPwd = request.getParameter("tempPwd");
                    if (name == null || name.trim().isEmpty() || tempPwd == null || tempPwd.trim().isEmpty()) {
                        json.put("code", 500);
                        json.put("msg", "请输入姓名和临时密码");
                    } else {
                        // 从pendingVisitors中查找临时密码对应的访客信息
                        VisitorInfo visitorInfo = pendingVisitors.get(tempPwd.trim());
                        if (visitorInfo == null) {
                            json.put("code", 501);
                            json.put("msg", "临时密码错误或不存在");
                        } else if (!visitorInfo.name.equals(name.trim())) {
                            json.put("code", 501);
                            json.put("msg", "姓名与临时密码不匹配");
                        } else {
                            // 创建进入记录
                            AccessRecord r = new AccessRecord();
                            r.setName(visitorInfo.name);
                            r.setIsOwner(0);
                            r.setIdCard(visitorInfo.idCard);
                            r.setPhone(visitorInfo.phone);
                            r.setTempPwd(tempPwd.trim());
                            recordDao.addRecord(r);

                            // 从pendingVisitors中移除已使用的临时密码
                            pendingVisitors.remove(tempPwd.trim());

                            json.put("code", 200);
                            json.put("msg", "验证通过，门已打开，欢迎来访！");
                        }
                    }
                }
            } else if ("visitorRegister".equals(action)) {
                // 访客登记：输入姓名、身份证、手机号，只生成临时密码，不创建进入记录
                String name = request.getParameter("name");
                String idCard = request.getParameter("idCard");
                String phone = request.getParameter("phone");
                if (name == null || name.trim().isEmpty()) {
                    json.put("code", 500);
                    json.put("msg", "请输入访客姓名");
                } else {
                    String tempPwd = genTempPwd(8);
                    // 存储访客信息到pendingVisitors
                    pendingVisitors.put(tempPwd, new VisitorInfo(
                        name.trim(),
                        idCard != null ? idCard.trim() : "",
                        phone != null ? phone.trim() : ""
                    ));

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
                    } else if (!recordDao.hasUnfinishedRecord(owner.getName())) {
                        // 检查是否有未离开的记录
                        json.put("code", 503);
                        json.put("msg", "您尚未进入小区，无法离开");
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
                    } else if (!recordDao.hasUnfinishedVisitorRecord(tempPwd.trim())) {
                        // 检查访客是否有未离开的记录
                        json.put("code", 503);
                        json.put("msg", "您尚未进入小区或已离开，无法再次离开");
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
