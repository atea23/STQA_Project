package Users;

import Model.User;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee extends User implements Serializable {

    private static final long serialVersionUID = -2899351992013979756L;
    private String birthday;
    private int id;
    private UserRole role;
    private double salary;
    private String phone;

    private LocalDate lastSalaryPaymentDate;


    public Employee(String firstName, String lastName, String email, String password, String gendre,
                    boolean isRemeberMe, String birthday, int id,
                    UserRole role, double salary, String phone) {
        super(firstName, lastName, email, password, gendre, isRemeberMe);
        this.birthday=birthday;
        this.id=id;
        this.role=role;
        this.phone=phone;
        this.salary=salary;
    }

    public Employee(String firstName, String lastName, String email, String password, String gendre,
                    boolean isRemeberMe, String birthday, int id,
                    UserRole role, double salary, String phone, LocalDate lastSalaryPaymentDate) {
        super(firstName, lastName, email, password, gendre, isRemeberMe);
        this.birthday=birthday;
        this.id=id;
        this.role=role;
        this.phone=phone;
        this.salary=salary;
        this.lastSalaryPaymentDate = lastSalaryPaymentDate;
    }

    public LocalDate getLastSalaryPaymentDate() {
        return lastSalaryPaymentDate;
    }

    public void setLastSalaryPaymentDate(LocalDate lastSalaryPaymentDate) {
        this.lastSalaryPaymentDate = lastSalaryPaymentDate;
    }

    public Employee() {
        super();
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public UserRole getRole() {
        return role;
    }

    @Override
    public void setRole(UserRole role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}