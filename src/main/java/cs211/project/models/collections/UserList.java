package cs211.project.models.collections;
import cs211.project.models.User;
import java.util.ArrayList;

public class UserList {
    private ArrayList<User> users;

    public UserList() {
        users = new ArrayList<User>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User findUserByUsername(String username) {
        for (User user : this.users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }


    public User getUser(String username) {
        for (User aUser : this.users) {
            if (username.equals(aUser.getUsername())) {
                return aUser;
            }
        }
        return null;
    }

    public void setProfileImageByUsername(String username, String profileImage) {
        findUserByUsername(username).setProfilePic(profileImage);
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }


}
