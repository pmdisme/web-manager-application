package cs211.project.models;
import javafx.scene.image.Image;
import java.io.File;

public class User {
    private String username;
    private String name;
    private String profilePic;
    private String password;

    public User(String username, String password,String name, String pic) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.profilePic = pic;
    }

    public String getUsername() {
        return this.username;
    }
    public String getPassword() {return this.password;}
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public Image getProfilePicture() {
        if(this.profilePic.equals("default.png")) {
            return new Image(getClass().getResource("/cs211/project/images/default.png").toExternalForm());
        }

        else {
            String filePath = "data/profile_picture/" + this.profilePic;
            File file = new File(filePath);
            return (new Image(file.toURI().toString()));
        }
    }

    public Image getProfilePicture(double v, double v1, boolean b, boolean b1) {
        if(this.profilePic.equals("default.png")) {
            return new Image(getClass().getResource("/cs211/project/images/default.png").toExternalForm(), v, v1, b, b1);
        }

        else {
            String filePath = "data/profile_picture/" + this.profilePic;
            File file = new File(filePath);
            return new Image(file.toURI().toString(), v, v1, b, b1);
        }
    }

    public String getProfilePictureName() {
        return this.profilePic;
    }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
}
