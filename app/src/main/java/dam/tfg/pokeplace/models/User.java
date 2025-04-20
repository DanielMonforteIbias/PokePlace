package dam.tfg.pokeplace.models;

public class User {
    private String userId;
    private String email;
    private String name;
    private String image;
    public User(){

    }
    public User(String userId, String email, String name, String image) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
