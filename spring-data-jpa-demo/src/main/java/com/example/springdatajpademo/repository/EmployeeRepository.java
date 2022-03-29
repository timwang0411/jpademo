package com.example.springdatajpademo.repository;

import com.example.springdatajpademo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Employee findFirstByOrderByEmpIdDesc();

    List<Employee> findAllByOrderByEmpIdAsc();
}
