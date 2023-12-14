package com.example.management;

import com.example.management.model.User;
import com.example.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class UsersPrepopulation {
    private UserRepository userRepository;

    @Autowired
    public UsersPrepopulation(UserRepository userRepository) {
        this.userRepository = userRepository;
        prepopulate();
    }

    public void prepopulate() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setEmail("user" + i + "@mail.com");
            user.setPassword("password" + i);
            users.add(user);
        }

        try {
            userRepository.saveAll(users);
        } catch (Exception ignored) {
        }
    }
}
