package cs211.project.services;

import cs211.project.models.Activity;
import cs211.project.models.collections.ActivityList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ParticipantActivityListFileDatasource implements Datasource<ActivityList> {
    private String directoryName;
    private String fileName;

    public ParticipantActivityListFileDatasource(String directoryName, String fileName) {
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
        ActivityList activities;
        activities = new ActivityList();
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
            while ((line = buffer.readLine()) != null) {
                if (line.equals("")) continue;
                String[] data = line.split(",");
                String eventOfActivityUUID = data[0].trim();
                String activityName = data[1].trim();
                String activityInformation = data[2].trim();
                try {
                    LocalTime activityStartTime = LocalTime.parse(data[3].trim());
                    LocalTime activityEndTime = LocalTime.parse(data[4].trim());
                    LocalDate activityDate = LocalDate.parse(data[5].trim());

                    activities.addNewActivityParticipant(eventOfActivityUUID, activityName, activityInformation, activityStartTime, activityEndTime, activityDate);
                } catch (DateTimeParseException e) {
                    System.err.println("Error parsing date or time on line: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        try {
            for (Activity activity : activityList.getActivities()) {
                String line = activity.getEventOfActivityUUID() + "," + activity.getActivityName() + "," + activity.getActivityInformation() + "," + activity.getActivityStartTime() + "," + activity.getActivityEndTime() + "," + activity.getActivityDate();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


