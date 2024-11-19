package org.example.employeemanagementsystem.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {

    @NotEmpty(message = "Please provide the employee's id")
    @Size(min = 3,message = "ID must be longer than two characters")
    private String id;

    @NotEmpty(message = "Please provide the employee's name")
    @Size(min = 5,message = "Name must be more than 4 characters")
    @Pattern(regexp = "^[a-zA-Z]+$",message = "Names can't contain numbers or symbols")
    private String name;

    @NotEmpty(message = "Please provide an email")
    @Email(message = "Please enter a valid email")
    private String email;

    @Pattern(regexp = "05[0-9]{8}",message = "Phone number must start with 05 and it must have 10 digits")
    @Size(min = 10,max = 10,message = "Phone numbers must be 10 digits")
    @Size()
    private String phoneNumber;

    @NotNull(message = "Please please provide the employee's age")
    @Min(value = 26,message = "Age must be more than 25")
    private Integer age;//used Integer wrapper to make @NotNull apply here

    @NotEmpty(message = "Please provide the employee's position")
    @Pattern(regexp = "^(supervisor|coordinator)$",message = "Position can only be coordinator or supervisor")
    private String position;

    @AssertFalse(message = "an employee can't start on leave")
    private boolean onLeave;

    @NotNull(message = "Please provide the employee's hiring date")
    @PastOrPresent(message = "hiring date can't be in the future")
    private LocalDate hireDate;

    @NotNull(message = "Please provide the employee's annual leave")
    @Positive(message = "Annual leave must be a positive number")
    private Integer annualLeave; //used Integer wrapper to make @NotNull apply here

}
