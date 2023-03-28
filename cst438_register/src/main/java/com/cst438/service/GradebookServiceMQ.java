package com.cst438.service;


import com.cst438.domain.CourseDTOG;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class GradebookServiceMQ extends GradebookService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    Queue gradebookQueue;


    public GradebookServiceMQ() {
        System.out.println("MQ grade book service");
    }

    // send message to grade book service about new student enrollment in course
    @Override
    public void enrollStudent(String student_email, String student_name, int course_id) {
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
        enrollmentDTO.studentEmail = student_email;
        enrollmentDTO.studentName = student_name;
        enrollmentDTO.course_id = course_id;

        rabbitTemplate.convertAndSend(gradebookQueue.getName(), enrollmentDTO);

        System.out.println("Message sent to gradebook service: " + enrollmentDTO);
    }

    @RabbitListener(queues = "registration-queue")
    public void receive(CourseDTOG courseDTO) {
        System.out.println("Receive course DTO data :" + courseDTO);

        // update student grades
        for (CourseDTOG.GradeDTO gradeDTO : courseDTO.grades) {
            Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(gradeDTO.student_email, courseDTO.course_id);

            if (enrollment == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No enrollment found for student with provided email and course id");
            }

            enrollment.setCourseGrade(gradeDTO.grade);
            enrollmentRepository.save(enrollment);
            System.out.println("Final grade updated: " + gradeDTO.student_email + " " + courseDTO.course_id + " " + gradeDTO.grade);
        }
    }

}