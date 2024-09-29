package cs211.project.models;

public class TeamParticipant {
    private String eventUUID;
    private String teamName;
    private String username;

    public TeamParticipant(String username, String eventUUID, String teamName) {
        this.username = username;
        this.eventUUID = eventUUID;
        this.teamName = teamName;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEventUUID() {
        return this.eventUUID;
    }

}
