package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/student")
    @Transactional
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    
    public Student addStudent (@RequestBody StudentDTO studentDTO) {
        
        Student existStudent = studentRepository.findByEmail(studentDTO.studentEmail);

        if (existStudent != null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email already exists.");
        } 
        else if (studentDTO.studentName == null || studentDTO.studentEmail == null)
        {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter Student Name and Email address to complete registration.");
        } else {
            Student student = new Student();
            student.setName(studentDTO.studentName);
            student.setEmail(studentDTO.studentEmail);
            student.setStatusCode(studentDTO.statusCode);
            student.setStatus(studentDTO.status);
            return studentRepository.save(student);
        }
    }

    @PutMapping(value = "/student/{student_id}")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    @Transactional

    public Student updateStudent(@PathVariable("student_id") int id, @RequestBody StudentDTO studentDTO) {
        Optional<Student> existStudent = studentRepository.findById(id);

        if (existStudent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found.");
        } else {
            Student student = existStudent.get();
            student.setStatusCode(studentDTO.statusCode);
            student.setStatus(studentDTO.status);
            return studentRepository.save(student);
        }
    }

    @GetMapping(value = "/student/{student_id}")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})

    public Student getStudent(@PathVariable("student_id") int id) {
        Optional<Student> existStudent = studentRepository.findById(id);

        if (existStudent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        } else {
            return existStudent.get();
        }
    }

    @GetMapping(value = "/student")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);

        return students;
    }
}
