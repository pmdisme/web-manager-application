package cs211.project.services;

import cs211.project.models.Activity;
import cs211.project.models.collections.ActivityList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TeamActivityListFileDatasource implements Datasource<ActivityList>{
    private String directoryName;
    private String fileName;

    public TeamActivityListFileDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }
    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public ActivityList readData() {
        ActivityList activities = new ActivityList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );
        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line = "";
        try {
            while ( (line = buffer.readLine()) != null ){

                if (line.equals("")) continue;

                String[] data = line.split(",");

                String eventUUID = data[0].trim();
                String teamName = data[1].trim();
                String activityName = data[2].trim();
                String activityInfo = data[3].trim().replace("//comma//", ",");
                String activityStatus = data[4].trim();
                String activityUUID = data[5].trim();

                activities.addNewActivityTeam(eventUUID, teamName, activityName, activityInfo, activityStatus, activityUUID);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        activities.sort(new TeamActivityStatusComparator());
        return activities;
    }

    @Override
    public void writeData(ActivityList activityList) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        activityList.sort(new TeamActivityStatusComparator());

        try {
            for (Activity activity : activityList.getActivities()) {
                String line = activity.getEventOfActivityUUID() + ","
                        + activity.getTeamOfActivityName() + ","
                        + activity.getActivityName() + ","
                        + activity.getActivityInformation().replace("\n", " ").replace(",", "//comma//") + ","
                        + activity.getActivityStatus() + ","
                        + activity.getActivityUUID();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
