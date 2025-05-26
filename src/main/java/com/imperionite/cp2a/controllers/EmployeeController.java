package com.imperionite.cp2a.controllers;

import com.imperionite.cp2a.entities.Employee;
import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.services.EmployeeService;
import com.imperionite.cp2a.services.UserService;

import com.imperionite.cp2a.dtos.AdminEmployeeDTO;
import com.imperionite.cp2a.dtos.EmployeeBasicInfoDTO;
import com.imperionite.cp2a.dtos.EmployeePartialDetailsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                Employee createdEmployee = employeeService.createEmployee(employee);
                return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                List<Employee> employees = employeeService.getAllEmployees();
                return new ResponseEntity<>(employees, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                Optional<Employee> employee = employeeService.getEmployeeById(id);
                return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AdminEmployeeDTO>> getAllEmployeesForAdmin(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                List<Employee> employees = employeeService.getAllEmployees();
                List<AdminEmployeeDTO> employeeDTOs = employees.stream()
                        .map(employee -> new AdminEmployeeDTO(
                                employee.getId(),
                                employee.getEmployeeNumber(),
                                employee.getLastName(),
                                employee.getFirstName(),
                                employee.getBirthday(),
                                employee.getUser()))
                        .collect(Collectors.toList());
                return new ResponseEntity<>(employeeDTOs, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/employeeNumber/{employeeNumber}")
    public ResponseEntity<Employee> getEmployeeByEmployeeNumber(@PathVariable String employeeNumber,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber);
                return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/basic-info") // basic employee info list
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EmployeeBasicInfoDTO>> getAllEmployeesBasicInfo() {
        List<Employee> employees = employeeService.getAllEmployees(); // Get all employees
        List<EmployeeBasicInfoDTO> employeeDTOs = employees.stream()
                .map(employee -> new EmployeeBasicInfoDTO( // Map to the DTO
                        employee.getEmployeeNumber(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getBirthday()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(employeeDTOs, HttpStatus.OK);
    }

    @GetMapping("/basic-info/employeeNumber/{employeeNumber}") // retrieve basic info by employee umber
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeBasicInfoDTO> getBasicInfoByEmployeeNumber(
            @PathVariable String employeeNumber, @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            Optional<Employee> employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber);
            if (employee.isPresent()) {
                EmployeeBasicInfoDTO dto = new EmployeeBasicInfoDTO(
                        employee.get().getEmployeeNumber(),
                        employee.get().getFirstName(),
                        employee.get().getLastName(),
                        employee.get().getBirthday());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDetails(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            Optional<Employee> employee = employeeService.findByUser(currentUser); // Find Employee by User

            if (employee.isPresent()) {
                return ResponseEntity.ok(employee.get()); // 200 OK - Return Employee details
            } else {
                // User is authenticated, but no Employee record is associated with them.
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden - User exists, but no
                                                                            // Employee record
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized - User not found
        }
    }

    @GetMapping("/partial/details")
    public ResponseEntity<List<EmployeePartialDetailsDTO>> getAllEmployeePartialDetails(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                List<EmployeePartialDetailsDTO> details = employeeService.getAllEmployeePartialDetails();
                return ResponseEntity.ok(details);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}