package com.example.nursery_store.repository;

import com.example.nursery_store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    void deleteById(long id);
}
