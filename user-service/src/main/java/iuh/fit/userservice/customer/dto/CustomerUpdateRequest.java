package iuh.fit.userservice.customer.dto;

import java.time.LocalDate;

import iuh.fit.userservice.customer.entity.Gender;

public class CustomerUpdateRequest {
   private LocalDate dateOfBirth;
    private Gender gender;
    private String skinType;

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