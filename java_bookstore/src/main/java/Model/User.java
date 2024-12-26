package Model;
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -3292385108400454436L;
    private String firstName, lastName, email, password, gendre;
    private boolean isRemeberMe;
    public static UserRole role;

    public User(String firstName, String lastName, String email, String password, String gendre,boolean isRemeberMe) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gendre = gendre;
        this.isRemeberMe = isRemeberMe;
    }

    public User() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGenre() {
        return gendre;
    }

    public void setGenre(String genre) {
        this.gendre = genre;
    }


    // can be used for log in mostly, just an example
    public boolean isRemeberMe() {
        return isRemeberMe;
    }

    public void setRemeberMe(boolean isRemeberMe) {
        this.isRemeberMe = isRemeberMe;
    }

    public enum UserRole {
        ADMIN,
        LIBRARIAN,
        MANAGER
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", password=" + password
                + ", genre=" + gendre +", isRemeberMe="
                + isRemeberMe + "]";
    }

}
