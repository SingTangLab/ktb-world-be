package com.singtanglab.ktbworld.service.user;

import com.singtanglab.ktbworld.dto.user.LoginRequest;
import com.singtanglab.ktbworld.dto.user.UserInfoDto;

import java.util.List;

public interface UserService {
    UserInfoDto login(LoginRequest request);
    List<UserInfoDto> findAllUsers();
    List<UserInfoDto> getAllUsers();
}
