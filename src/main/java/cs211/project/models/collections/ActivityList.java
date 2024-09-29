package cs211.project.models.collections;
import cs211.project.models.Activity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivityList {
    ArrayList<Activity> activities;

    public ActivityList() {
        activities = new ArrayList<>();
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void addNewActivityTeam(String eventOfActivityUUID, String teamOfActivityName, String activityName, String activityInformation, String activityStatus, String activityUUID) {
        eventOfActivityUUID = eventOfActivityUUID.trim();
        teamOfActivityName = teamOfActivityName.trim();
        activityName = activityName.trim();
        activityInformation = activityInformation.trim();
        if (!eventOfActivityUUID.equals("") && !teamOfActivityName.equals("") && !activityName.equals("") && !activityInformation.equals("")) {
            Activity exist = findActivityByObject(new Activity(eventOfActivityUUID, teamOfActivityName, activityName, activityInformation, activityStatus, activityUUID));
            if (exist == null) {
                activities.add(new Activity(eventOfActivityUUID, teamOfActivityName, activityName, activityInformation, activityStatus, activityUUID));
            }
        }
    }

    public void addNewActivityParticipant(String eventOfActivityUUID, String activityName, String activityInformation, LocalTime activityStartTime, LocalTime activityEndTime, LocalDate activityDate)

    {
        eventOfActivityUUID = eventOfActivityUUID.trim();
        activityName = activityName.trim();
        activityInformation = activityInformation.trim();
        if (!activityName.equals("") && !activityInformation.equals("")) {
            Activity newActivity = findActivityByObject(new Activity(eventOfActivityUUID, activityName, activityInformation, activityStartTime, activityEndTime, activityDate));
            if (newActivity == null) {
                activities.add(new Activity(eventOfActivityUUID, activityName, activityInformation, activityStartTime, activityEndTime, activityDate));

            }
        }
    }



    public Activity findActivityByObject(Activity activity) {
        for (Activity activity1 : activities) {
            if (activity1.equals(activity)) {
                return activity1;
            }
        }
        return null;
    }

    public Activity findActivityByUUID(String activityUUID) {
        for (Activity activity : activities) {
            if (activityUUID.equals(activity.getActivityUUID())) {
                return activity;
            }
        }
        return null;
    }

    public void removeActivity(Activity activity) {
        this.activities.remove(activity);
    }

    public void setActivityStatusByUUID(String activityUUID, String activityStatus){
        findActivityByUUID(activityUUID).setActivityStatus(activityStatus);
    }

    public void sort(Comparator<Activity> activityComparator){
        Collections.sort(activities, activityComparator);
    }
}
