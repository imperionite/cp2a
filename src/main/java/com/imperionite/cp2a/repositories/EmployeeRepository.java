package com.imperionite.cp2a.repositories;

import com.imperionite.cp2a.entities.Employee;
import com.imperionite.cp2a.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    Optional<Employee> findById(Long id);
    Optional<Employee> findByUser(User user);
}