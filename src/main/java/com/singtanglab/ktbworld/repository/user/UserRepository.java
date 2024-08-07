package com.singtanglab.ktbworld.repository.user;

import com.singtanglab.ktbworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findById(Integer id);
    List<User> findAll();
}
