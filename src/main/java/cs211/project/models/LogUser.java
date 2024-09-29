package cs211.project.models;

public class LogUser extends User {
    private String logTime;
    private String logDate;
    public LogUser(String username, String password,String name, String pic, String logDate, String logTime) {
        super(username,password,name,pic);
        this.logTime = logTime;
        this.logDate = logDate;
    }

    public String getLogTime() {
        return this.logTime;
    }

    public String getLogDate() { return this.logDate;}
}
