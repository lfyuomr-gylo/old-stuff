package com.github.lfyuomr.gylo.bostongene.task2.data;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConcurrentHashMapUserRepository implements UserRepository {
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User getUserByEmail(String email) {
        return usersByEmail.get(email);
    }

    @Override
    public boolean createUser(User user) {
        return usersByEmail.putIfAbsent(user.getEmail(), user) == null;
    }

    @Override
    public boolean deleteUser(String email) {
        return usersByEmail.remove(email) != null;
    }
}
