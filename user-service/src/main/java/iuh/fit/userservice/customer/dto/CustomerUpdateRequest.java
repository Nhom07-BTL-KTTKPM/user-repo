package iuh.fit.userservice.customer.dto;

import java.time.LocalDate;

import iuh.fit.userservice.customer.entity.Gender;

public class CustomerUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String skinType;

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getSkinType() {
        return skinType;
    }

}