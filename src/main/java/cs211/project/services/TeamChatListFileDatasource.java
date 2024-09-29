package cs211.project.services;

import cs211.project.models.TeamChat;
import cs211.project.models.collections.TeamChatList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TeamChatListFileDatasource implements Datasource<TeamChatList>{
    private String directoryName;
    private String fileName;

    public TeamChatListFileDatasource(String directoryName, String fileName) {
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
    public TeamChatList readData() {
        TeamChatList teamChats = new TeamChatList();
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
                String username = data[2].trim();
                String message = data[3].trim().replace("//comma//", ",");
                LocalDateTime time = LocalDateTime.parse(data[4].trim());
                String activityUUID = data[5].trim();

                teamChats.addNewChat(eventUUID, teamName, username, message, time, activityUUID);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        teamChats.sort();
        return teamChats;
    }

    @Override
    public void writeData(TeamChatList teamChatList) {
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
        teamChatList.sort();
        try {
            for (TeamChat teamChat : teamChatList.getTeamChats()) {
                String line = teamChat.getEventUUID() + ","
                        + teamChat.getTeamName() + ","
                        + teamChat.getUsername() + ","
                        + teamChat.getMessage().replace(",", "//comma//") + ","
                        + teamChat.getTime() + ","
                        + teamChat.getActivityUUID();
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
