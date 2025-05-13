package com.imperionite.cp2a.repositories;

import com.imperionite.cp2a.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeNumberAndDateBetween(String employeeNumber, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT MIN(a.date) FROM Attendance a") // JPQL query
    LocalDate findMinDate();

    @Query("SELECT MAX(a.date) FROM Attendance a") // JPQL query
    LocalDate findMaxDate();

    List<Attendance> findByEmployeeNumber(String employeeNumber);

    // Optimized query for weekly hours:
    @Query("SELECT a FROM Attendance a WHERE a.employeeNumber = :employeeNumber AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findAttendancesForWeek(@Param("employeeNumber") String employeeNumber,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get Attendance by employee number and date
    Optional<Attendance> findByEmployeeNumberAndDate(String employeeNumber, LocalDate date);
}