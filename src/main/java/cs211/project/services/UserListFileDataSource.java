package cs211.project.services;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class UserListFileDataSource implements Datasource<UserList> {
    private String directoryName;
    private String fileName;
    public UserListFileDataSource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    @Override
    public UserList readData() {
        UserList ls = new UserList();
        try {
            String filePath = directoryName + File.separator + fileName;
            FileReader fileReader = new FileReader(filePath);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line = "";
            try {
                while ((line = buffer.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    String[] data = line.split(",");
                    String username = data[0].trim();
                    String password = data[1].trim();
                    String name = data[2].trim();
                    String pic = data[3].trim();
                    ls.addUser(new User(username,password, name, pic));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    @Override
    public void writeData(UserList userList) {
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
            for (User user : userList.getUsers()) {
                String line = user.getUsername() + "," + user.getPassword() + "," + user.getName() + "," + user.getProfilePictureName();
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
        File pictureDirectory = new File(directoryName + File.separator + "profile_picture");
        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }
    }
}
