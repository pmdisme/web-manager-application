package cs211.project.services;

import cs211.project.models.Activity;

import java.util.Comparator;

public class TeamActivityStatusComparator implements Comparator<Activity>{

    @Override
    public int compare(Activity o1, Activity o2) {
        if (o1.getActivityStatus().equals("Ended") && o2.getActivityStatus().equals("Ended")){
            return o1.getActivityName().compareTo(o2.getActivityName());
        }
        if (o1.getActivityStatus().equals("Ended")){
            return 1;
        }
        if (o2.getActivityStatus().equals("Ended")){
            return -1;
        }
        return o1.getActivityName().compareTo(o2.getActivityName());
    }
}
