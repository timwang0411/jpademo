package com.example.springdatajpademo.service;

import com.example.springdatajpademo.entity.Employee;
import com.example.springdatajpademo.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public List<Employee> listAll() {
        return employeeRepository.findAllByOrderByEmpIdAsc();
    }

    @Transactional
    public void add() {
        Employee employee = Employee.builder()
                .email("test@mail")
                .firstName("first_name")
                .lastName("last_name")
                .mobileNo("0912345678")
                .createTime(LocalDateTime.now())
                .updateTime(null)
                .build();
        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteByEmpId(Integer empId) {
        //if employee id exist
        if (employeeRepository.findById(empId).isPresent()) {
            employeeRepository.deleteById(empId);
        } else {
            logger.warn("emp_id not fount");
        }
    }

    @Transactional
    public void updateByEmpId(Integer empId) {
        if (employeeRepository.findById(empId).isPresent()) {
            Employee employee = employeeRepository.getById(empId);
            employee = Employee.builder()
                    .empId(empId)
                    .email("update@mail")
                    .firstName("first_name_update")
                    .lastName("last_name_update")
                    .mobileNo("0987654321")
                    .createTime(employee.getCreateTime())
                    .updateTime(LocalDateTime.now())
                    .build();
            employeeRepository.save(employee);
        } else {
            logger.warn("emp_id not fount");
        }
    }

    public List<Employee> findByEmail(String email){
        List<Employee> employeesLs = employeeRepository.findByEmailContains(email);
        return employeesLs;
    }

}
