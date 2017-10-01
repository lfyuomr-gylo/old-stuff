package com.github.lfyuomr.gylo.bostongene.task2.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lfyuomr.gylo.bostongene.task2.data.User
import com.github.lfyuomr.gylo.bostongene.task2.data.UserRepository
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MyUsersSelectionTest extends Specification {
    @Autowired
    MockMvc mockMvc

    @MockBean
    UserRepository repositoryMock

//    UserRepository repositoryMock = Mock() // вариант с моком из spock, а не из mockito

    @Autowired
    ObjectMapper objectMapper

    User user

    void setup() throws Exception {
        user = User.builder()
                .email("foo@bar.com")
                .firstName("Foo")
                .lastName("Bar")
                .birthDate(LocalDate.of(1997, 7, 16))
                .password("foobar")
                .build()

//        repositoryMock.getUserByEmail(user.getEmail()) >> user // вариант с моком из spock, а не из mockito

        when(repositoryMock.getUserByEmail(user.getEmail())).thenReturn(user)
    }

    void "invalid email should cause bad request status"() throws Exception {
        when:
        def response = mockMvc.perform(get("/users?email=foobar"))
        then:
        response.andExpect(status().isBadRequest())
    }

    void "non-existing user should be not found"() throws Exception {
        when:
        def response = mockMvc.perform(get("/users?email=baz@baz.com"))
        then:
        response.andExpect(status().isNotFound())
    }

    void "actual user should found"() throws Exception {
        when:
        def response = mockMvc.perform(get("/users?email=" + user.getEmail()))
        then:
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
    }
}