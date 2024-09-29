package cs211.project.models;

import java.time.LocalDateTime;

public class TeamChat implements Comparable<TeamChat>{
    private String teamName;
    private String eventUUID;
    private String username;
    private String message;
    private String activityUUID;
    private LocalDateTime time;

    public TeamChat(String eventUUID, String teamName, String username, String message, LocalDateTime time,String activityUUID) {
        this.teamName = teamName;
        this.eventUUID = eventUUID;
        this.username = username;
        this.message = message;
        this.time = time;
        this.activityUUID = activityUUID;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getEventUUID() {
        return eventUUID;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getActivityUUID() {
        return activityUUID;
    }

    public LocalDateTime getTime() {
        return time;
    }


    @Override
    public int compareTo(TeamChat o) {
        return this.time.compareTo(o.time);
    }
}
