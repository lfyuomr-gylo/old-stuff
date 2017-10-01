package com.github.lfyuomr.gylo.bostongene.task2.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lfyuomr.gylo.bostongene.task2.data.User;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsersInsertionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void insertionWorks() throws Exception {
        val user = User.builder()
                       .email("foo@bar.com")
                       .firstName("Foo")
                       .lastName("Bar")
                       .birthDate(LocalDate.of(1997, 7, 16))
                       .password("foobar")
                       .build();
        insertUser(user)
                .andExpect(status().isOk())
                .andExpect(matchJsonIgnorePassword(user));
    }

    @Test
    public void reinsertionFails() throws Exception {
        val user = User.builder()
                       .email("foo@baz.com")
                       .firstName("Foo")
                       .lastName("Baz")
                       .birthDate(LocalDate.of(1997, 7, 16))
                       .password("password")
                       .build();
        insertUser(user)
                .andExpect(status().isOk())
                .andExpect(matchJsonIgnorePassword(user));
        insertUser(user)
                .andExpect(status().isConflict());
    }

    @Test
    public void insertedIsAvailable() throws Exception {
        val user = User.builder()
                       .email("alex@worm.ru")
                       .firstName("Alex")
                       .lastName("Worm")
                       .birthDate(LocalDate.of(1954, 1, 2))
                       .password("alex has great password")
                       .build();
        val insertedUserJson = insertUser(user).andExpect(status().isOk())
                                               .andExpect(matchJsonIgnorePassword(user))
                                               .andReturn()
                                               .getResponse()
                                               .getContentAsString();
        mockMvc.perform(get("/users?email=" + user.getEmail()))
               .andExpect(status().isOk())
               .andExpect(content().json(insertedUserJson));
    }

    @Test
    public void deletionOfNotInsertedUserFails() throws Exception {
        mockMvc.perform(delete("/users?email=" + "vsjkjvhs@svjkvsdjevwh.io"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void deletionOfInsertedSucceeds() throws Exception {
        val user = User.builder()
                       .email("inserted@deleted.com")
                       .firstName("Inserted")
                       .lastName("Deleted")
                       .birthDate(LocalDate.of(1987, 6, 5))
                       .password("userPassword")
                       .build();

        insertUser(user).andExpect(status().isOk());
        mockMvc.perform(delete("/users?email=" + user.getEmail())).andExpect(status().isOk());
        mockMvc.perform(get("/users?email=" + user.getEmail())).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users?email=" + user.getEmail())).andExpect(status().isNotFound());
    }

    public ResultActions insertUser(User user) throws Exception {
        return mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(user)));

    }

    private ResultMatcher matchJsonIgnorePassword(User user) {
        return result -> {
            val responseBody = result.getResponse().getContentAsString();
            val responseUser = objectMapper.readValue(responseBody, User.class);
            assertEquals(user.getEmail(), responseUser.getEmail());
            assertEquals(user.getFirstName(), responseUser.getFirstName());
            assertEquals(user.getLastName(), responseUser.getLastName());
            assertEquals(user.getBirthDate(), responseUser.getBirthDate());
        };
    }
}
