package com.singtanglab.ktbworld.service.user;

import com.singtanglab.ktbworld.dto.user.LoginRequest;
import com.singtanglab.ktbworld.dto.user.UserInfoDto;
import com.singtanglab.ktbworld.entity.User;
import com.singtanglab.ktbworld.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserInfoDto login(LoginRequest request) {
        String nickname = request.nickname();
        String password = request.password();

        try {
            Optional<User> userOptional = userRepository.findByNickname(nickname);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getPassword().equals(password)) {
                    return new UserInfoDto(user.getId(), user.getNickname());
                } else {
                    log.error("Invalid password for user: {}", nickname);
                    throw new RuntimeException("Invalid password");
                }
            } else {
                log.error("User not found: {}", nickname);
                throw new RuntimeException("User not found");
            }
        } catch (RuntimeException e) {
            log.error("Exception during login: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional  // 트랜잭션을 관리
    public List<UserInfoDto> findAllUsers() {
        try {
            List<User> users = userRepository.findAll();  // 모든 유저를 조회
            return users.stream()  // 각 유저에 대해
                    .map(user -> new UserInfoDto(user.getId(), user.getNickname()))  // UserInfoDto로 변환
                    .collect(Collectors.toList());  // 리스트로 수집
        } catch (Exception e) {
            log.error("Exception during fetching all users: {}", e.getMessage());  // 유저 조회 중 예외 발생 시 로그
            throw new RuntimeException("Failed to fetch all users", e);  // 예외를 다시 던짐
        }
    }

    @Transactional  // 트랜잭션을 관리
    public List<UserInfoDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserInfoDto(user.getId(), user.getNickname()))
                .collect(Collectors.toList());
    }
}
