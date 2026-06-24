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
}