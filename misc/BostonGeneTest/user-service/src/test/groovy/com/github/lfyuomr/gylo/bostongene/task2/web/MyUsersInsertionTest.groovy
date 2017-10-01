package com.github.lfyuomr.gylo.bostongene.task2.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lfyuomr.gylo.bostongene.task2.data.User
import lombok.val
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import spock.lang.Specification

import java.time.LocalDate

import static org.junit.Assert.assertEquals
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class MyUsersInsertionTest extends Specification {
    def "always green test"() {
        expect: true
    }

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    void "insertion works correctly"() {
        given:
        def user = User.builder()
                .email("foo@bar.com")
                .firstName("Foo")
                .lastName("Bar")
                .birthDate(LocalDate.of(1997, 7, 16))
                .password("foobar")
                .build()

        expect:
        insertUser(user)
                .andExpect(status().isOk())
                .andExpect(matchJsonIgnorePassword(user))
    }

    void "reinsertion causes CONFLICT(409) status"() {
        given:
        def user = User.builder()
                .email("foo@baz.com")
                .firstName("Foo")
                .lastName("Baz")
                .birthDate(LocalDate.of(1997, 7, 16))
                .password("password")
                .build()
        expect:
        insertUser(user)
                .andExpect(status().isOk())
                .andExpect(matchJsonIgnorePassword(user))
        insertUser(user)
                .andExpect(status().isConflict())
    }

    void "inserted user is available via get-method"() {
        given:
        def user = User.builder()
                .email("alex@worm.ru")
                .firstName("Alex")
                .lastName("Worm")
                .birthDate(LocalDate.of(1954, 1, 2))
                .password("alex has great password")
                .build();
        def insertedUserJson = insertUser(user).andExpect(status().isOk())
                .andExpect(matchJsonIgnorePassword(user))
                .andReturn()
                .getResponse()
                .getContentAsString()

        expect:
        mockMvc.perform(get("/users?email=" + user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().json(insertedUserJson))
    }

    void "attempt to delete non-existent user causes NOT_FOUND(404) status"() {
        expect:
        mockMvc.perform(delete("/users?email=" + "vsjkjvhs@svjkvsdjevwh.io"))
                .andExpect(status().isNotFound())
    }

    void "deletion of actual user succeeds and deletes the user indeed"() {
        given:
        def user = User.builder()
                .email("inserted@deleted.com")
                .firstName("Inserted")
                .lastName("Deleted")
                .birthDate(LocalDate.of(1987, 6, 5))
                .password("userPassword")
                .build()

        expect:
        insertUser(user).andExpect(status().isOk())
        mockMvc.perform(delete("/users?email=" + user.getEmail())).andExpect(status().isOk());
        mockMvc.perform(get("/users?email=" + user.getEmail())).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users?email=" + user.getEmail())).andExpect(status().isNotFound());
    }

    ResultActions insertUser(User user) {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
    }

    ResultMatcher matchJsonIgnorePassword(User user) {
        return { result ->
            def responseBody = result.getResponse().getContentAsString()
            def responseUser = objectMapper.readValue(responseBody, User.class)
            assertEquals(user.getEmail(), responseUser.getEmail())
            assertEquals(user.getFirstName(), responseUser.getFirstName())
            assertEquals(user.getLastName(), responseUser.getLastName())
            assertEquals(user.getBirthDate(), responseUser.getBirthDate())
        }
    }
}