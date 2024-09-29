package cs211.project.services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cs211.project.models.collections.EventList;
import cs211.project.models.collections.UserList;

public class JoinEventFileDataSource implements Datasource<List<String[]>> {
    private String directoryName;
    private String fileName;
    private Datasource<EventList> EventListDatasource;
    private EventList eventList;
    private Datasource<UserList> userListDatasource;
    private UserList userList;

    public JoinEventFileDataSource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();

        EventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = EventListDatasource.readData();

        userListDatasource = new UserListFileDataSource("data", "userData.csv");
        userList = userListDatasource.readData();
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
    public List<String[]> readData() {
        List<String[]> data = new ArrayList<>();
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

        try {
            String line = "";
            while ((line = buffer.readLine()) != null) {
                if (line.equals("")) continue;
                data.add(line.split(","));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public void writeData(List<String[]> data) {
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
            for (String[] entry : data) {
                String line = String.join(",", entry);
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

    public int countParticipantsForEvent(String eventUUID) {
        List<String[]> data = readData();
        int count = 0;
        for (String[] entry : data) {
            if (entry[1].equals(eventUUID)) {
                count++;
            }
        }
        return count;
    }


}
