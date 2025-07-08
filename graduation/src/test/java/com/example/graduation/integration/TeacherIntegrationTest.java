package com.example.graduation.integration;

import com.example.graduation.dto.teacher.CreateTeacherDTO;
import com.example.graduation.dto.teacher.UpdateTeacherDTO;

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
class TeacherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "TEACHER")
    void getTeachersTest() throws Exception {
        mockMvc.perform(get("/api/teachers"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "TEACHER")
    void createTeacherTest() throws Exception {
        CreateTeacherDTO teacherDTO = new CreateTeacherDTO();
        teacherDTO.setFirstName("John");
        teacherDTO.setLastName("Doe");
        teacherDTO.setTeacherNumber("T111111");

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.teacherNumber").value("T111111"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void updateTeacherTest() throws Exception {
        //Teacher to Create
        CreateTeacherDTO createDTO = new CreateTeacherDTO();
        createDTO.setFirstName("Janet");
        createDTO.setLastName("Smith");
        createDTO.setTeacherNumber("T222222");

        //Create query
        String createResponse = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        //Data to Update with
        UpdateTeacherDTO updateDTO = new UpdateTeacherDTO();
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");

        //Update query
        mockMvc.perform(put("/api/teachers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void deleteTeacherTest() throws Exception {
        // Create first
        CreateTeacherDTO createDTO = new CreateTeacherDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setTeacherNumber("T333333");

        String response = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Now delete
        mockMvc.perform(delete("/api/teachers/" + id))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(roles = "TEACHER")
    void getTeacherByIdTest() throws Exception {
        CreateTeacherDTO createDTO = new CreateTeacherDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setTeacherNumber("T999999");

        String response = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/teachers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.teacherNumber").value("T999999"));
    }

    //Get Teacher to show on edit form
    @Test
    @WithMockUser(roles = "TEACHER")
    void getTeacherForEditTest() throws Exception {
        CreateTeacherDTO createDTO = new CreateTeacherDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setTeacherNumber("T555555");

        String response = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/teachers/" + id + "/edit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
}
