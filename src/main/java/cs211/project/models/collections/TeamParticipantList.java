package cs211.project.models.collections;

import cs211.project.models.TeamParticipant;

import java.util.ArrayList;

public class TeamParticipantList{
    ArrayList<TeamParticipant> teamParticipants;

    public TeamParticipantList() {
        teamParticipants = new ArrayList<>();
    }


    public boolean addNewTeamParticipant(String username, String eventUUID, String teamName) {
        username = username.trim();
        eventUUID = eventUUID.trim();
        teamName = teamName.trim();
        if (!username.equals("") && !eventUUID.equals("") && !teamName.equals("")) {
            TeamParticipant exist = findTeamParticipantByUsernameAndEventUUIDAndTeam(username, eventUUID, teamName);
            if (exist == null) {
                teamParticipants.add(new TeamParticipant(username, eventUUID, teamName));
                return true;
            }
        }
        return false;
    }

    public TeamParticipant findTeamParticipantByUsernameAndEventUUIDAndTeam(String username, String eventUUID, String teamName) {
        for (TeamParticipant teamParticipant : teamParticipants) {
            if (teamParticipant.getUsername().equals(username) && teamParticipant.getTeamName().equals(teamName) && teamParticipant.getEventUUID().equals(eventUUID)) {
                return teamParticipant;
            }
        }
        return null;
    }

    public int getTeamParticipantCountByEventUUIDAndTeamName(String eventUUID, String teamName) {
        int count = 0;
        for (TeamParticipant teamParticipant : teamParticipants) {
            if (teamParticipant.getEventUUID().equals(eventUUID) && teamParticipant.getTeamName().equals(teamName)) {
                count++;
            }
        }
        return count - 1;
    }

    public boolean checkUserInTeam(String username, String eventUUID, String teamName) {
        for (TeamParticipant teamParticipant : teamParticipants) {
            if (teamParticipant.getUsername().equals(username) && teamParticipant.getEventUUID().equals(eventUUID) && teamParticipant.getTeamName().equals(teamName)) {
                return true;
            }
        }
        return false;
    }

    public void removeTeamParticipant(TeamParticipant teamParticipant) {
        this.teamParticipants.remove(teamParticipant);
    }

    public ArrayList<TeamParticipant> getTeamParticipants() {
        return teamParticipants;
    }
}
