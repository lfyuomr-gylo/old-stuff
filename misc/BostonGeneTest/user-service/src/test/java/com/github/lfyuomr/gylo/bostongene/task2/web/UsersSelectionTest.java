package com.github.lfyuomr.gylo.bostongene.task2.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lfyuomr.gylo.bostongene.task2.data.User;
import com.github.lfyuomr.gylo.bostongene.task2.data.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsersSelectionTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository repositoryMock;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = User.builder()
                   .email("foo@bar.com")
                   .firstName("Foo")
                   .lastName("Bar")
                   .birthDate(LocalDate.of(1997, 7, 16))
                   .password("foobar")
                   .build();
        when(repositoryMock.getUserByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    public void emailIsValidated() throws Exception {
        mockMvc.perform(get("/users?email=foobar")).andExpect(status().isBadRequest());
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/users?email=baz@baz.com")).andExpect(status().isNotFound());
    }

    @Test
    public void actualUserFound() throws Exception {
        mockMvc.perform(get("/users?email=" + user.getEmail()))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }
}
