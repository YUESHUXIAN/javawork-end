package community.entity;

import java.sql.Timestamp;
public class OwnerApply {
    private Integer id;
    private Integer ownerId;
    private String newPhone;
    private String newRoom;
    private String newPwd;
    private Timestamp applyTime;
    private Integer status;
    // 关联查询字段
    private String ownerName;
    private String ownerIdCard;
    private Integer ownerBuildId;
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Integer getOwnerId() {return ownerId;}
    public void setOwnerId(Integer ownerId) {this.ownerId = ownerId;}
    public String getNewPhone() {return newPhone;}
    public void setNewPhone(String newPhone) {this.newPhone = newPhone;}
    public String getNewRoom() {return newRoom;}
    public void setNewRoom(String newRoom) {this.newRoom = newRoom;}
    public String getNewPwd() {return newPwd;}
    public void setNewPwd(String newPwd) {this.newPwd = newPwd;}
    public Timestamp getApplyTime() {return applyTime;}
    public void setApplyTime(Timestamp applyTime) {this.applyTime = applyTime;}
    public Integer getStatus() {return status;}
    public void setStatus(Integer status) {this.status = status;}
    public String getOwnerName() {return ownerName;}
    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}
    public String getOwnerIdCard() {return ownerIdCard;}
    public void setOwnerIdCard(String ownerIdCard) {this.ownerIdCard = ownerIdCard;}
    public Integer getOwnerBuildId() {return ownerBuildId;}
    public void setOwnerBuildId(Integer ownerBuildId) {this.ownerBuildId = ownerBuildId;}
}