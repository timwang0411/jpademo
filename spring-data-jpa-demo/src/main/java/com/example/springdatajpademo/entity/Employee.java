package com.example.springdatajpademo.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    @Id
    @SequenceGenerator(name = "employee_emp_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int empId;

    private String email;

    private String firstName;

    private String lastName;

    private String mobileNo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}