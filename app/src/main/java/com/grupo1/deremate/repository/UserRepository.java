package com.grupo1.deremate.repository;

import com.grupo1.deremate.models.UserDTO;

public interface UserRepository {
    void saveUser(UserDTO user);
    UserDTO getUser();
    void clearUser();
}
