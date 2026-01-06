package com.NTG.mirathy.service;

import com.NTG.mirathy.Entity.User;
import com.NTG.mirathy.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow();
    }
}
