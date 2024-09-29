package cs211.project.models;
import java.time.LocalDate;
import java.time.LocalTime;


public class Activity {
    private String eventOfActivityUUID;
    private String teamOfActivityName;
    private String activityName;
    private String activityInformation;
    private String activityStatus;
    private LocalTime activityStartTime;
    private LocalTime activityEndTime;
    private LocalDate activityDate;
    private String activityUUID;

    public Activity(String eventOfActivityUUID, String teamOfActivityName, String activityName, String activityInformation, String activityStatus, String activityUUID) {
        this.eventOfActivityUUID = eventOfActivityUUID;
        this.teamOfActivityName = teamOfActivityName;
        this.activityName = activityName;
        this.activityInformation = activityInformation;
        this.activityStatus = activityStatus;
        this.activityUUID = activityUUID;
    }

    public Activity(String eventOfActivityUUID, String activityName, String activityInformation, LocalTime activityStartTime, LocalTime activityEndTime, LocalDate activityDate) {
        this.eventOfActivityUUID = eventOfActivityUUID;
        this.activityName = activityName;
        this.activityInformation = activityInformation;
        this.activityStartTime = activityStartTime;
        this.activityEndTime = activityEndTime;
        this.activityDate = activityDate;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getActivityInformation() {
        return activityInformation;
    }

    public String getEventOfActivityUUID() {
        return eventOfActivityUUID;
    }

    public String getTeamOfActivityName() {
        return teamOfActivityName;
    }


    public LocalTime getActivityEndTime() {
        return activityEndTime;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public LocalTime getActivityStartTime() {
        return activityStartTime;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }
    public boolean isActivity(Activity activity) {
        if (activity.getActivityName().equals(this.getActivityName()) && activity.getEventOfActivityUUID().equals(this.getEventOfActivityUUID()) && activity.getTeamOfActivityName().equals(this.getTeamOfActivityName())) {
            return true;
        }
        return false;
    }

    public String getActivityUUID() {
        return activityUUID;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) obj;

        String activityName = activity.getActivityName();
        String eventOfActivityUUID = activity.getEventOfActivityUUID();

        if (this.teamOfActivityName != null){ //เป็น activity ของ team
            return this.activityUUID.equals(activity.getActivityUUID());
        } else { //เป็น activity ของ event
            return this.activityName.equals(activityName) && this.eventOfActivityUUID.equals(eventOfActivityUUID);
        }
    }

}
