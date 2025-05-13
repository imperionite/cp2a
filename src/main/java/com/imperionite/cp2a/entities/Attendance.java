package com.imperionite.cp2a.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects; // Import for equals and hashCode

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number", nullable = false)
    @NotNull(message = "Employee number cannot be null")
    private String employeeNumber; // temporary direct employee info storage

    @Column(name = "last_name", nullable = false)
    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Column(name = "first_name", nullable = false)
    @NotNull(message = "First name cannot be null")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @Column(name = "log_in", nullable = false)
    @NotNull(message = "Login time cannot be null")
    private LocalTime logIn;

    @Column(name = "log_out", nullable = false)
    @NotNull(message = "Logout time cannot be null")
    private LocalTime logOut;


    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", date=" + date +
                ", logIn=" + logIn +
                ", logOut=" + logOut +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Attendance that = (Attendance) o;
        return Objects.equals(employeeNumber, that.employeeNumber) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, date);
    }

}