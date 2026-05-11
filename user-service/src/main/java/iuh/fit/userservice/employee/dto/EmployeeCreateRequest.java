package iuh.fit.userservice.employee.dto;

public class EmployeeCreateRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
   
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getFullName() {
        return fullName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
