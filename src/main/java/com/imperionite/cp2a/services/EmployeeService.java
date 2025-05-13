package com.imperionite.cp2a.services;

import com.imperionite.cp2a.entities.Employee;
import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.repositories.EmployeeRepository;
import com.imperionite.cp2a.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Employee createEmployee(Employee employee) {
        Optional<User> userOpt = userRepository.findById(employee.getUser().getId());
        if (userOpt.isPresent()) {
            employee.setUser(userOpt.get());
            return employeeRepository.save(employee);
        } else {
            throw new EntityNotFoundException("User not found for ID: " + employee.getUser().getId()); 
        }
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> findByUser(User user) {
        return employeeRepository.findByUser(user);
    }

    public Optional<Employee> getEmployeeByEmployeeNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber);
    }
}