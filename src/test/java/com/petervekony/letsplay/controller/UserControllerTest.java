package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username="admin", roles="admin")
    public void testGetUserById_found() throws Exception {
        // Given
        UserModel testUser = new UserModel();
        testUser.setId("12345");
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");

        when(userService.getUserById("12345")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/12345"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin", roles="admin")
    public void testGetUserById_notFound() throws Exception {
        when(userService.getUserById("nonexistentID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/nonexistentID"))
                .andExpect(status().isNotFound());
    }
}
