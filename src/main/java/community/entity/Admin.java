package community.entity;

public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String ukeyCode;

    // getter setter
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getUkeyCode() {return ukeyCode;}
    public void setUkeyCode(String ukeyCode) {this.ukeyCode = ukeyCode;}
}