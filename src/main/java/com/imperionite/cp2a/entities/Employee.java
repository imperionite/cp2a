package com.imperionite.cp2a.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number", unique = true, nullable = false)
    private String employeeNumber;

    @Column(name = "last_name", nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(name = "birthday", nullable = false)
    @NotNull
    private LocalDate birthday;

    @Column(name = "address", nullable = false)
    @NotNull
    private String address;

    @Column(name = "phone_number", unique = true, nullable = false)
    @NotNull
    private String phoneNumber;

    @Column(name = "sss", unique = true, nullable = false)
    @NotNull
    private String sss;

    @Column(name = "philhealth", unique = true, nullable = false)
    @NotNull
    private String philhealth;

    @Column(name = "tin", unique = true, nullable = false)
    @NotNull
    private String tin;

    @Column(name = "pagibig", unique = true, nullable = false)
    @NotNull
    private String pagibig;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "position", nullable = false)
    @NotNull
    private String position;

    @Column(name = "immediate_supervisor", nullable = false)
    @NotNull
    private String immediateSupervisor;

    @Column(name = "basic_salary", nullable = false)
    @NotNull
    private BigDecimal basicSalary;

    @Column(name = "rice_subsidy", nullable = false)
    @NotNull
    private BigDecimal riceSubsidy;

    @Column(name = "phone_allowance", nullable = false)
    @NotNull
    private BigDecimal phoneAllowance;

    @Column(name = "clothing_allowance", nullable = false)
    @NotNull
    private BigDecimal clothingAllowance;

    @Column(name = "gross_semi_monthly_rate", nullable = false)
    @NotNull
    private BigDecimal grossSemiMonthlyRate;

    @Column(name = "hourly_rate", nullable = false)
    @NotNull
    private BigDecimal hourlyRate;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDate createdAt; // Changed to LocalDateTime if you want time as well

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt; // Changed to LocalDateTime if you want time as well


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

   // Override toString() for better readability.
   @Override
   public String toString() {
       return "Employee{" +
               "id=" + id +
               ", employeeNumber='" + employeeNumber + '\'' +
               ", lastName='" + lastName + '\'' +
               ", firstName='" + firstName + '\'' +
               ", status=" + status +
               '}';
   }

   // Override equals() and hashCode() based on unique fields.
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (!(o instanceof Employee)) return false;
       Employee employee = (Employee) o;
       return id != null && id.equals(employee.id);
   }

   @Override
   public int hashCode() {
       return getClass().hashCode();
   }
}