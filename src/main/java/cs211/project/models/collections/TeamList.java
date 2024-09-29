package cs211.project.models.collections;

import cs211.project.models.Team;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TeamList {
    private ArrayList<Team> teams;

    public TeamList() {
        teams = new ArrayList<>();
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }
    public boolean addNewTeam(String eventUUID, String teamName, int maxParticipants, LocalDate startJoinDate, LocalTime startJoinTime, LocalDate closingJoinDate, LocalTime endJoinTime, String teamOwnerUsername, String headOfTeamUsername) {
        eventUUID = eventUUID.trim();
        teamName = teamName.trim();
        if (!eventUUID.equals("") && !teamName.equals("") && maxParticipants > 0 && startJoinDate != null && closingJoinDate != null && !headOfTeamUsername.equals("")) {
            Team exist = findEventByEventUUIDAndTeamName(eventUUID, teamName);
            if (exist == null) {
                teams.add(new Team(eventUUID, teamName, maxParticipants, startJoinDate, startJoinTime, closingJoinDate, endJoinTime, teamOwnerUsername, headOfTeamUsername));
                return true;
            }
            return false;
        }
        return false;
    }

    public Team findEventByEventUUIDAndTeamName(String eventUUID, String teamName){
        for (Team team : this.teams) {
            if (eventUUID.equals(team.getEventUUID()) && teamName.equals(team.getTeamName())) {
                return team;
            }
        }
        return null;
    }

    public Team findTeamByObject(Team team) {
        for (Team team1 : teams) {
            if (team1.equals(team)) {
                return team1;
            }
        }
        return null;
    }

    public void sort(){
        Collections.sort(teams);
    }

    public void sort(Comparator<Team> comparator){
        Collections.sort(teams, comparator);
    }
}
