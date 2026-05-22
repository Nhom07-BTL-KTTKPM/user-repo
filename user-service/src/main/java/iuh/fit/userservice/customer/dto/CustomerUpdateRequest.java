package iuh.fit.userservice.customer.dto;

import java.time.LocalDate;
import java.util.List;

import iuh.fit.userservice.customer.entity.Gender;

public class CustomerUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String skinType;
    private List<String> skinConcerns;

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

    public List<String> getSkinConcerns() {
        return skinConcerns;
    }

    public void setSkinConcerns(List<String> skinConcerns) {
        this.skinConcerns = skinConcerns;
    }

}