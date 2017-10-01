package com.github.lfyuomr.gylo.bostongene.task2.data;


public interface UserRepository {
    User getUserByEmail(String email);

    boolean createUser(User user);

    boolean deleteUser(String email);
}
