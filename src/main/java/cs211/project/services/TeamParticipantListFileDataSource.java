package cs211.project.services;
import cs211.project.models.TeamParticipant;
import cs211.project.models.collections.TeamParticipantList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TeamParticipantListFileDataSource implements Datasource<TeamParticipantList>{
    private String directoryName;
    private String fileName;

    public TeamParticipantListFileDataSource(String directoryName, String fileName) {
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
    public TeamParticipantList readData() {
        TeamParticipantList teamParticipantList = new TeamParticipantList();
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

                String username = data[0].trim();
                String eventUUID = data[1].trim();
                String teamName = data[2].trim();

                teamParticipantList.addNewTeamParticipant(username, eventUUID, teamName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return teamParticipantList;
    }

    @Override
    public void writeData(TeamParticipantList teamParticipantList) {
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
            for (TeamParticipant teamParticipant : teamParticipantList.getTeamParticipants()) {
                String line = teamParticipant.getUsername() + "," + teamParticipant.getEventUUID() + "," + teamParticipant.getTeamName();
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
