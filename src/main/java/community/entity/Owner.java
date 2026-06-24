package community.entity;

public class Owner {
    private Integer id;
    private String name;
    private String idCard;
    private String phone;
    private Integer buildId;
    private String roomNo;
    private String entryPwd;
    private Integer isConfirm; // 0未确认，1已确认

    // getter & setter
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getIdCard() {return idCard;}
    public void setIdCard(String idCard) {this.idCard = idCard;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public Integer getBuildId() {return buildId;}
    public void setBuildId(Integer buildId) {this.buildId = buildId;}
    public String getRoomNo() {return roomNo;}
    public void setRoomNo(String roomNo) {this.roomNo = roomNo;}
    public String getEntryPwd() {return entryPwd;}
    public void setEntryPwd(String entryPwd) {this.entryPwd = entryPwd;}
    public Integer getIsConfirm() {return isConfirm;}
    public void setIsConfirm(Integer confirm) {isConfirm = confirm;}
}