package community.entity;

public class Building {
    private Integer id;
    private String buildName;
    private String buildNo;

    public Building() {}
    public Building(Integer id, String buildName, String buildNo) {
        this.id = id;
        this.buildName = buildName;
        this.buildNo = buildNo;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getBuildName() { return buildName; }
    public void setBuildName(String buildName) { this.buildName = buildName; }
    public String getBuildNo() { return buildNo; }
    public void setBuildNo(String buildNo) { this.buildNo = buildNo; }
    @Override
    public String toString() {
        return "Building{id="+id+",buildName='"+buildName+"',buildNo='"+buildNo+"'}";
    }
}
