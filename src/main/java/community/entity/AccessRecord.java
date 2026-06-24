package community.entity;

import java.sql.Timestamp;
public class AccessRecord {
    private Integer id;
    private String name;
    private Integer isOwner;
    private String idCard;
    private String phone;
    private Timestamp enterTime;
    private Timestamp leaveTime;
    private String tempPwd;
    private Integer ownerId;

    // getter setter
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Integer getIsOwner() {return isOwner;}
    public void setIsOwner(Integer owner) {isOwner = owner;}
    public String getIdCard() {return idCard;}
    public void setIdCard(String idCard) {this.idCard = idCard;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public Timestamp getEnterTime() {return enterTime;}
    public void setEnterTime(Timestamp enterTime) {this.enterTime = enterTime;}
    public Timestamp getLeaveTime() {return leaveTime;}
    public void setLeaveTime(Timestamp leaveTime) {this.leaveTime = leaveTime;}
    public String getTempPwd() {return tempPwd;}
    public void setTempPwd(String tempPwd) {this.tempPwd = tempPwd;}
    public Integer getOwnerId() {return ownerId;}
    public void setOwnerId(Integer ownerId) {this.ownerId = ownerId;}
}