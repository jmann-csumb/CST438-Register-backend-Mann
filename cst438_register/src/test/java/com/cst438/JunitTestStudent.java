package com.cst438;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {StudentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest

public class JunitTestStudent {
    public static final int TEST_STD_ID = 10;
    public static final int TEST_STD_ID_DNE = 30;
    public static final String TEST_STD_EMAIL = "test@csumb.edu";
    public static final String TEST_STD_NAME = "test";
    public static final int TEST_HOLD = 1;
    public static final String TEST_HOLD_STR = "Student on hold";

    @MockBean
    StudentRepository studentRepository;

    @Autowired
    private MockMvc mvc;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            System.out.println("STRING " + str);
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addStudent() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setEmail(TEST_STD_EMAIL);
        student.setName(TEST_STD_NAME);
        student.setStatusCode(0);
        student.setStudent_id(TEST_STD_ID);

        when(studentRepository.save(any())).thenReturn(student);

        
        StudentDTO studentDTO = new StudentDTO();

        // Check Missing data
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/student")
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        
        assertEquals(400, response.getStatus());

        // Add missing data
        studentDTO.studentEmail = TEST_STD_EMAIL;
        studentDTO.studentName = TEST_STD_NAME;

        // HTTP post request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/student")
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        
        // verify returned data and saved
        Student result = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(TEST_STD_ID, result.getStudent_id());
        assertTrue(student.equals(result));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    public void updateStudent() throws Exception {
        MockHttpServletResponse response;

        Student studentInitial = new Student();
        studentInitial.setEmail(TEST_STD_EMAIL);
        studentInitial.setName(TEST_STD_NAME);
        studentInitial.setStatusCode(0);
        studentInitial.setStatus(null);
        studentInitial.setStudent_id(TEST_STD_ID);

        Student student = new Student();
        student.setEmail(TEST_STD_EMAIL);
        student.setName(TEST_STD_NAME);
        student.setStatusCode(TEST_HOLD);
        student.setStatus(TEST_HOLD_STR);
        student.setStudent_id(TEST_STD_ID);

        when(studentRepository.save(any())).thenReturn(student);
        given(studentRepository.findById(TEST_STD_ID_DNE)).willReturn(Optional.empty());
        given(studentRepository.findById(TEST_STD_ID)).willReturn(Optional.of(studentInitial));

        // DTO to add the student.
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.status = TEST_HOLD_STR;
        studentDTO.statusCode = TEST_HOLD;

        // If student not exist
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STD_ID_DNE)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        // Update a student with status
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STD_ID)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(200, response.getStatus());

        Student result = fromJsonString(response.getContentAsString(), Student.class);
        assertTrue(student.equals(result));
        verify(studentRepository).save(any(Student.class));

        //Uodate student hold status
        student.setStatusCode(0);
        student.setStatus(null);
        when(studentRepository.save(any())).thenReturn(student);

        studentDTO.status = null;
        studentDTO.statusCode = 0;

        //Update student hold
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STD_ID)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        result = fromJsonString(response.getContentAsString(), Student.class);
        assertTrue(student.equals(result));
    }
}