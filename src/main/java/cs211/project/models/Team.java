package cs211.project.models;
import java.time.LocalDate;
import java.time.LocalTime;


public class Team implements Comparable {
    private String headOfTeamUsername;
    private String eventUUID;
    private String teamName;
    private int maxParticipants;
    private LocalDate startJoinDate;
    private LocalTime startJoinTime;
    private LocalDate closingJoinDate;
    private LocalTime endJoinTime;
    private String teamOwnerUsername;

    public Team(String eventUUID, String teamName, int maxParticipants, LocalDate startJoinDate, LocalTime startJoinTime, LocalDate closingJoinDate, LocalTime endJoinTime, String teamOwnerUsername, String headOfTeamUsername) {
        this.eventUUID = eventUUID;
        this.teamName = teamName;
        this.maxParticipants = maxParticipants;
        this.startJoinDate = startJoinDate;
        this.startJoinTime = startJoinTime;
        this.closingJoinDate = closingJoinDate;
        this.endJoinTime = endJoinTime;
        this.teamOwnerUsername = teamOwnerUsername;
        this.headOfTeamUsername = headOfTeamUsername;
    }

    public String getEventUUID() {
        return eventUUID;
    }
    public String getTeamName() {
        return teamName;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public LocalDate getStartJoinDate() {
        return startJoinDate;
    }

    public LocalDate getClosingJoinDate() {
        return closingJoinDate;
    }

    public String getHeadOfTeamUsername() {
        return headOfTeamUsername;
    }

    public String getTeamOwnerUsername() {return teamOwnerUsername;}

    public LocalTime getStartTime() {
        return startJoinTime;
    }

    public LocalTime getEndTime() {
        return endJoinTime;
    }

    public void setHeadOfTeamUsername(String headOfTeamUsername) {
        this.headOfTeamUsername = headOfTeamUsername;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            Team team = (Team) obj;
            return team.getTeamName().equals(this.getTeamName()) && team.getEventUUID().equals(this.getEventUUID());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Team) {
            Team team = (Team) o;
            if (this.getEventUUID().equals(team.getEventUUID())) {
                return this.getTeamName().compareTo(team.getTeamName());
            }
            return this.getEventUUID().compareTo(team.getEventUUID());
        }
        return 0;
    }
}
