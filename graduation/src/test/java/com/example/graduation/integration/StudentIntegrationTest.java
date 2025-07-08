package com.example.graduation.integration;

import com.example.graduation.dto.student.CreateStudentDTO;
import com.example.graduation.dto.student.UpdateStudentDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "TEACHER")
    void getStudentsTest() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "TEACHER")
    void createStudentTest() throws Exception {
        CreateStudentDTO studentDTO = new CreateStudentDTO();
        studentDTO.setFirstName("John");
        studentDTO.setLastName("Doe");
        studentDTO.setStudentNumber("F111111");

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.studentNumber").value("F111111"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void updateStudentTest() throws Exception {
        //Student to Create
        CreateStudentDTO createDTO = new CreateStudentDTO();
        createDTO.setFirstName("Janet");
        createDTO.setLastName("Smith");
        createDTO.setStudentNumber("F222222");

        //Create query
        String createResponse = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        //Data to Update with
        UpdateStudentDTO updateDTO = new UpdateStudentDTO();
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");

        //Update query
        mockMvc.perform(put("/api/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void deleteStudentTest() throws Exception {
        // Create first
        CreateStudentDTO createDTO = new CreateStudentDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setStudentNumber("F333333");

        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Now delete
        mockMvc.perform(delete("/api/students/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void getStudentByIdTest() throws Exception {
        CreateStudentDTO createDTO = new CreateStudentDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setStudentNumber("F999999");

        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.studentNumber").value("F999999"));
    }

    //Get Student to show on edit form
    @Test
    @WithMockUser(roles = "TEACHER")
    void getStudentForEditTest() throws Exception {
        CreateStudentDTO createDTO = new CreateStudentDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setStudentNumber("F555555");

        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/students/" + id + "/edit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
}
