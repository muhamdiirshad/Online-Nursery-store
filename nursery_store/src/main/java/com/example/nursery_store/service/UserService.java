package com.example.nursery_store.service;

import com.example.nursery_store.entity.User;
import com.example.nursery_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {

        return userRepository.save(user);
    }
    public User login(String email, String password) {

        return  userRepository.findByEmailAndPassword(email, password);

    }
    public User updateUser(User user) {

        return userRepository.save(user);
    }
    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


}