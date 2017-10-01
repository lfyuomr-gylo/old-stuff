package com.github.lfyuomr.gylo.bostongene.task2.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Email;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor // required by Jackson
public class User {
    @Email String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String password;

    public User withPassword(String newPassword) {
        return new User(email, firstName, lastName, birthDate, newPassword);
    }
}
