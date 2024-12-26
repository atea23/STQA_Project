package Users;

import Model.User;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import Users.Employee;

public class Administrator extends Employee {

    private static final long serialVersionUID = 123456789L;
    private static final String EMPLOYEE_FILE = "employees.dat";

    public Administrator(String firstName, String lastName, String email, String password, String gender,
                         boolean isRememberMe, String birthday, int id,
                         UserRole role, double salary, String phone) {
        super(firstName, lastName, email, password, gender, isRememberMe, birthday, id, role, salary, phone);
    }

    public Administrator() {
        super();
    }

    public boolean registerEmployee(Employee newEmployee) {
        List<Employee> employees = readEmployeeData();
        employees.add(newEmployee);
        LocalDate lastSalaryPaymentDate = LocalDate.now(); // Initialize with the current date
        newEmployee.setLastSalaryPaymentDate(lastSalaryPaymentDate);
        return writeEmployeeData(employees);
    }

    public boolean modifyEmployee(int id, Employee modifiedEmployee) {
        List<Employee> employees_list = readEmployeeData();
        for (int i = 0; i < employees_list.size(); i++) {
            if (employees_list.get(i).getId() == id) {
                employees_list.set(i, modifiedEmployee);
                return writeEmployeeData(employees_list);
            }
        }
        return false;
    }

    public List<Double> accessSalary() {
        List<Employee> employees = readEmployeeData();
        List<Double> salaries = new ArrayList<>();

        for (Employee employee : employees) {
            salaries.add(employee.getSalary());
        }

        return salaries;
    }

    public List<Double> accessSalaryForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Employee> employees = readEmployeeData();
        List<Double> salaries = new ArrayList<>();

        for (Employee employee : employees) {
            LocalDate lastSalaryDate = employee.getLastSalaryPaymentDate();
            if (lastSalaryDate != null && !lastSalaryDate.isBefore(startDate) && !lastSalaryDate.isAfter(endDate)) {
                // Salary falls within the specified period
                int monthsBetween = (int) ChronoUnit.MONTHS.between(lastSalaryDate, endDate);
                double totalSalaryForPeriod = employee.getSalary() * (monthsBetween + 1); // Including the current month
                salaries.add(totalSalaryForPeriod);
            }
        }
        return salaries;
    }


    public boolean deleteEmployee(int id) {
        List<Employee> employees = readEmployeeData();
        employees.removeIf(employee -> employee.getId() == id);
        return writeEmployeeData(employees);
    }

    public List<Employee> readEmployeeData() {
        try {

            File file = new File(EMPLOYEE_FILE);

            if (!file.exists()) {
                System.out.println("Employee file not found. Creating a new one.");
                try {
                    if (file.createNewFile()) {
                        System.out.println("File created: " + file.getName());
                    } else {
                        System.out.println("Failed to create the file.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while creating the file.");
                    e.printStackTrace();
                }
            }

            try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
                Object obj = reader.readObject();

                if (obj instanceof List<?>) {
                    return (List<Employee>) obj;
                }

                System.out.println("Invalid data format in the file");
            } catch (EOFException e) {
                System.out.println("Read all employees from the file");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    public boolean writeEmployeeData(List<Employee> employees) {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(EMPLOYEE_FILE))) {
            writer.writeObject(employees);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
