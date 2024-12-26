package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import Model.User;

public class UserController {

    private ArrayList<User> users;
    private File file;

    public UserController() {
        users = new ArrayList<>();
        file = new File("users.bin");
        if (file.exists()) {
            readUsers();
        }
    }

    private void readUsers() {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            users = (ArrayList<User>) ois.readObject();
            fis.close();
            ois.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeUsers() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(users);
            oos.close();
            fos.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addUser(User u, User.UserRole role) {
        u.setRole(role);
        this.users.add(u);
        writeUsers();
    }

    public boolean signUp(String firstName, String lastName, String email, String password, String verifyPassword,
                          String gendre, boolean isRemeberMe, User.UserRole role) {

        if (password.equals(verifyPassword)) {
            User u = new User(firstName, lastName, email, password, gendre, isRemeberMe);
            u.setRole(role);
            this.addUser(u, role);
            return true;
        }

        return false;
    }

    public void print() {
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i));
        }
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                setUserRole(user, password);
                return user;
            }
        }
        return null;
    }

    private void setUserRole(User user, String password) {
        if (password.equals("admin")) {
            user.setRole(User.UserRole.ADMIN);
        } else if (password.equals("librarian")) {
            user.setRole(User.UserRole.LIBRARIAN);
        } else if (password.equals("manager")) {
            user.setRole(User.UserRole.MANAGER);
        }
    }
}