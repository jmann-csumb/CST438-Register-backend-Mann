package com.cst438.domain;

import java.util.Objects;

public class StudentDTO {
    public String studentEmail;
    public String studentName;
    public int statusCode;
    public String status;
    public int student_id;

    public StudentDTO() {
        this.studentEmail = null;
        this.studentName = null;
        this.statusCode = 0;
        this.status = null;
        this.student_id = 0;
    }


    @Override
    public String toString() {
        return "StudentDTO{" +
                "studentEmail='" + studentEmail + '\'' +
                ", studentName='" + studentName + '\'' +
                ", statusCode=" + statusCode +
                ", status='" + status + '\'' +
                ", student_id=" + student_id +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDTO that = (StudentDTO) o;
        return student_id == that.student_id && studentEmail.equals(that.studentEmail) && studentName.equals(that.studentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentEmail, studentName, student_id);
    }
}