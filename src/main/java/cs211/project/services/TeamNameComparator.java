package cs211.project.services;

import cs211.project.models.Team;

import java.util.Comparator;

public class TeamNameComparator implements Comparator<Team>{
    @Override
    public int compare(Team o1, Team o2) {
        if (o1.getEventUUID().equals(o2.getEventUUID())) {
            return o1.getTeamName().compareTo(o2.getTeamName());
        }
        return o1.getEventUUID().compareTo(o2.getEventUUID());
    }
}
