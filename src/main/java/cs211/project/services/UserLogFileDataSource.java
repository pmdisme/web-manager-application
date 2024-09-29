package cs211.project.services;

import cs211.project.models.LogUser;
import cs211.project.models.User;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;

public class UserLogFileDataSource implements Datasource<UserList> {

    private String directoryName;
    private String fileName;

    public UserLogFileDataSource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }
    @Override
    public UserList readData() {
        Datasource<UserList> userListDatasource = new UserListFileDataSource("data", "userData.csv");
        UserList userList = userListDatasource.readData();
        UserList users = new UserList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการอ่านไฟล์
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
        User temp = null;
        try {
            while ( (line = buffer.readLine()) != null ){
                if (line.equals("")) continue;
                String[] data = line.split(",");
                String username = data[0];
                String date = data[1];
                String time = data[2];
                temp = userList.getUser(username);
                users.addUser(new LogUser(temp.getUsername(), temp.getPassword(), temp.getName(), temp.getProfilePictureName(), date,time));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.reverse(users.getUsers());
        UserList finalUsers = new UserList();
        ArrayList<String> pastUsers = new ArrayList<String>();
        for(User user : users.getUsers()) {
            if(!pastUsers.contains(user.getUsername())) {
                finalUsers.addUser(user);
                pastUsers.add(user.getUsername());
            }
        }
        return finalUsers;
    }

    @Override
    public void writeData(UserList userList) {
        String username = "";
        String filePath = directoryName + File.separator + fileName;
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        String log = "";
        for(User user : userList.getUsers()) {
            username = user.getUsername();
        }
        log = username + "," + LocalDate.now(thaiTimeZone).toString() + "," +  LocalTime.now(thaiTimeZone).toString().substring(0,8);;
        FileWriter fileWriter = null;
        PrintWriter out = null;
        try {
            fileWriter = new FileWriter(filePath,true);
            out = new PrintWriter(new BufferedWriter(fileWriter));
            out.println(log);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
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
}
